/*
 * Copyright (C) 2023-2024 ConnectorIO Sp. z o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.thing4.addons.model.facade;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.XtextPackage;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.impl.BinaryGrammarResourceFactoryImpl;
import org.eclipse.xtext.service.OperationCanceledManager;
import org.openhab.core.config.core.ConfigDescriptionRegistry;
import org.openhab.core.internal.i18n.I18nProviderImpl;
import org.openhab.core.internal.service.BundleResolverImpl;
import org.openhab.core.model.core.EventType;
import org.openhab.core.model.core.ModelRepositoryChangeListener;
import org.openhab.core.model.core.internal.ModelRepositoryImpl;
import org.openhab.core.model.core.internal.SafeEMFImpl;
import org.openhab.core.model.thing.ThingRuntimeModule;
import org.openhab.core.model.thing.internal.GenericThingProvider;
import org.openhab.core.model.thing.thing.ThingPackage;
import org.openhab.core.service.ReadyMarker;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.i18n.ChannelTypeI18nLocalizationService;
import org.openhab.core.thing.type.ChannelTypeRegistry;
import org.openhab.core.thing.type.ThingTypeRegistry;
import org.openhab.core.thing.xml.internal.XmlChannelTypeProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thing4.addons.model.facade.osgi.DummyBundleContext;
import org.thing4.addons.model.facade.osgi.DummyComponentContext;
import org.thing4.addons.model.facade.osgi.DummyThingHandlerFactory;

/**
 * The official parser calls openHAB apis to trigger parsing of thing file.
 *
 * Be aware, amount of code needed to do so is massive.
 */
public class ThingParserFacade {

  public static final String TEST_BUNDLE = "test-bundle";

  static {
    if (!Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("ecore")) {
      Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    }
    if (!Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xmi")) {
      Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
    }
    if (!Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xtextbin")) {
      Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xtextbin", new BinaryGrammarResourceFactoryImpl());
    }
    if (!EPackage.Registry.INSTANCE.containsKey(XtextPackage.eNS_URI)) {
      EPackage.Registry.INSTANCE.put(XtextPackage.eNS_URI, XtextPackage.eINSTANCE);
    }
    if (!EPackage.Registry.INSTANCE.containsKey("https://openhab.org/model/Thing")) {
      EPackage.Registry.INSTANCE.put("https://openhab.org/model/Thing", ThingPackage.eINSTANCE);
    }
  }

  private final Logger logger = LoggerFactory.getLogger(ThingParserFacade.class);

  public List<Thing> parse(byte[] contents) throws Exception {
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(OperationCanceledManager.class).toInstance(new OperationCanceledManager());
      }
    }, new ThingRuntimeModule());

    Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("things", injector.getInstance(IResourceFactory.class));

    ModelRepositoryImpl repository = new ModelRepositoryImpl(new SafeEMFImpl());
    BundleContext bundleContext = new DummyBundleContext(TEST_BUNDLE);
    I18nProviderImpl i18nProvider = new I18nProviderImpl(new DummyComponentContext(bundleContext));

    XmlChannelTypeProvider xmlChannelTypeProvider = new XmlChannelTypeProvider(new ChannelTypeI18nLocalizationService(i18nProvider));
    ChannelTypeRegistry channelTypeRegistry = new ChannelTypeRegistry() {{
      addChannelTypeProvider(xmlChannelTypeProvider);
    }};

    ThingTypeRegistry thingTypeRegistry = new ThingTypeRegistry(channelTypeRegistry);
    ConfigDescriptionRegistry configDescriptionRegistry = new ConfigDescriptionRegistry();

    GenericThingProvider thingProvider = new GenericThingProvider() {{
      setLocaleProvider(i18nProvider);
      setModelRepository(repository);
      setThingTypeRegistry(thingTypeRegistry);
      setChannelTypeRegistry(channelTypeRegistry);
      setConfigDescriptionRegistry(configDescriptionRegistry);
      addThingHandlerFactory(new DummyThingHandlerFactory(thingTypeRegistry, configDescriptionRegistry));
      setBundleResolver(new BundleResolverImpl() {
        @Override
        public Bundle resolveBundle(Class<?> clazz) {
          return bundleContext.getBundle();
        }
      });
      onReadyMarkerAdded(new ReadyMarker("openhab.xmlThingTypes", TEST_BUNDLE));
    }};

    repository.addModelRepositoryChangeListener(new ModelRepositoryChangeListener() {
      @Override
      public void modelChanged(String modelName, EventType type) {
        logger.info("Model level event {}, model {}", type, modelName);
      }
    });
    repository.addOrRefreshModel("test.things", new ByteArrayInputStream(contents));

    thingProvider.activate();

    return new ArrayList<>(thingProvider.getAll());
  }

}
