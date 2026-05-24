package org.thing4.core.grqphql.internal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thing4.core.grqphql.Handler;

@Component(service = HttpServlet.class)
public class GraphqlServlet extends HttpServlet {

  public static final String PATH = "/graphql";
  private final Logger logger = LoggerFactory.getLogger(GraphqlServlet.class);
  private final List<Handler> handlers = new CopyOnWriteArrayList<>();
  private final HttpService httpService;
  private final AtomicReference<GraphQL> gql = new AtomicReference<>();

  private final ObjectMapper jackson = new ObjectMapper();
  private byte[] bytes = new byte[0];

  @Activate
  public GraphqlServlet(BundleContext context, @Reference HttpService httpService) throws ServletException, NamespaceException {
    this.httpService = httpService;
    httpService.registerServlet(PATH, this, new Hashtable(), httpService.createDefaultHttpContext());
    this.gql.set(build());
  }

  private GraphQL build() {
    Builder runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();
    List<Handler> handlers = new ArrayList<>(this.handlers);
    if (handlers.isEmpty()) {
      handlers.add(new DummyHandler());
    }
    handlers.add(new ScalarHandler());

    TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
    for (Handler handler : handlers) {
      typeRegistry.merge(handler.getTypeDefinitionRegistry());
      handler.contributeWiring(runtimeWiringBuilder);
    }

    GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiringBuilder.build());
    return GraphQL.newGraphQL(schema)
      .instrumentation(new ChainedInstrumentation(List.of(
        new IntrospectionInstrumentation(true),
        new MaxQueryDepthInstrumentation(20),
        new MaxQueryComplexityInstrumentation(200)
      )))
      .build();
  }

  @Deactivate
  public void deactivate() {
    httpService.unregister(PATH);
  }

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void addHandler(Handler handler) {
    this.handlers.add(handler);
    this.gql.set(build());
  }

  public void removeHandler(Handler handler) {
    this.handlers.remove(handler);
    try {
      this.gql.set(build());
    } catch (Exception e) {
      logger.error("Could not build schema", e);
    }
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (req.getMethod().equals("GET")) {
      if (bytes.length == 0) {
        InputStream template = getClass().getResourceAsStream("/static/index.html");
        if (template != null) {
          ByteArrayOutputStream data = new ByteArrayOutputStream();
          try (InputStreamReader isr = new InputStreamReader(template); OutputStreamWriter os = new OutputStreamWriter(data)) {
            isr.transferTo(os);
            os.flush();
          }
          bytes = data.toByteArray();
        }
      }

      if (bytes.length > 0) {
        try (InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(bytes))) {
          resp.setHeader("Content-Type", "text/html");
          PrintWriter writer = resp.getWriter();
          isr.transferTo(writer);
          writer.flush();
          return;
        }
      }

      resp.sendError(404);
      return;
    }
    super.service(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String query = req.getParameter("query");
    if (query == null || query.isBlank()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required query parameter");
      return;
    }
    String operationName = req.getParameter("operationName");
    String rawVariables   = req.getParameter("variables");
    Map<String, Object> variables = null;
    if (rawVariables != null && !rawVariables.isBlank()) {
      variables = parseVariables(rawVariables);
    }
    GraphQLRequest request = new GraphQLRequest(query, operationName);
    request.setVariables(variables);

    if (req.isAsyncSupported()) {
      AsyncContext asyncContext = req.startAsync();
      executeAsync(request).whenComplete((answer, error) -> {
        try {
          if (error != null) {
            logger.warn("Failure while handling request {}", request);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error.getMessage());
            return;
          }
          writeJson(resp, answer);
        } catch (IOException e) {
          logger.error("Failed to write async response", e);
        } finally {
          asyncContext.complete();
        }
      });
      return;
    }
    writeJson(resp, execute(request).getData());
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    GraphQLRequest request = jackson.readValue(req.getInputStream(), GraphQLRequest.class);
    writeJson(resp, execute(request));
  }

  private ExecutionResult execute(GraphQLRequest request) {
    return gql.get().execute(request.toInput(loaderRegistry()));
  }

  private DataLoaderRegistry loaderRegistry() {
    DataLoaderRegistry registry = new DataLoaderRegistry();
    for (Handler handler : handlers) {
      handler.getDataLoaderRegistry().ifPresent(reg -> {
        for (Entry<String, DataLoader<?, ?>> entry : reg.getDataLoadersMap().entrySet()) {
          String key = entry.getKey();
          DataLoader<?, ?> value = entry.getValue();
          registry.register(key, value);
        }
      });
    }
    return registry;
  }

  private CompletableFuture<ExecutionResult> executeAsync(GraphQLRequest request) {
    return gql.get().executeAsync(request.toInput(loaderRegistry()));
  }

  private void writeJson(HttpServletResponse resp, Object payload) throws IOException {
    resp.setContentType("application/json");
    resp.setStatus(HttpServletResponse.SC_OK);
    try (PrintWriter writer = resp.getWriter()) {
      jackson.writeValue(writer, payload);
    }
  }

  private Map<String, Object> parseVariables(String raw) {
    try {
      return jackson.readValue(raw, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      logger.warn("Could not parse variables JSON: {}", e.getMessage());
      return Collections.emptyMap();
    }
  }

  public static class GraphQLRequest {
    private final Map<String, Object> variables = new LinkedHashMap<>();
    private final Map<String, Object> extensions = new LinkedHashMap<>();
    private final String query;
    private final String operationName;

    @JsonCreator
    public GraphQLRequest(@JsonProperty("query") String query, @JsonProperty("operationName") String operationName) {
      this.query = query;
      this.operationName = operationName;
    }

    public void setVariables(Map<String, Object> v) {
      if (v != null) {
        this.variables.putAll(v);
      }
    }

    public Map<String, Object> getExtensions() {
      return extensions;
    }

    public void setExtensions(Map<String, Object> e) {
      if (e != null) {
        this.extensions.putAll(e);
      }
    }

    public ExecutionInput toInput(DataLoaderRegistry loaderRegistry) {
      return ExecutionInput.newExecutionInput(query)
        .operationName(operationName)
        .variables(variables)
        .extensions(extensions)
        .dataLoaderRegistry(loaderRegistry)
        .build();
    }

    @Override
    public String toString() {
      return "GraphQLRequest [" + operationName + ": " + query + "]";
    }
  }
}
