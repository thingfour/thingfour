package org.thing4.core.grqphql.internal;

import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;

public class IntrospectionInstrumentation extends SimplePerformantInstrumentation {

    private final boolean allowed;

    public IntrospectionInstrumentation(boolean allowed) {
        this.allowed = allowed;
    }

    @Override
    public DataFetcher<?> instrumentDataFetcher(
            DataFetcher<?> dataFetcher,
            InstrumentationFieldFetchParameters parameters,  // ← correct class
            InstrumentationState state) {

        // field name lives on the environment, not on parameters directly
        String fieldName = parameters.getEnvironment().getField().getName();

        if (!fieldName.startsWith("__")) {
            return dataFetcher;
        }

        if (allowed) {
            return dataFetcher;
        }

        throw new RuntimeException("Forbidden");
    }
}