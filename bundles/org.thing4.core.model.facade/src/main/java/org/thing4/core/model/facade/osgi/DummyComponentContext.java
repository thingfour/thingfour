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
package org.thing4.core.model.facade.osgi;

import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.ComponentInstance;

public class DummyComponentContext implements ComponentContext {

  private final BundleContext context;

  public DummyComponentContext(BundleContext context) {
    this.context = context;
  }

  @Override
  public Dictionary<String, Object> getProperties() {
    return new Hashtable<>();
  }

  @Override
  public Object locateService(String s) {
    return null;
  }

  @Override
  public Object[] locateServices(String s) {
    return new Object[0];
  }

  @Override
  public BundleContext getBundleContext() {
    return context;
  }

  @Override
  public Bundle getUsingBundle() {
    return context.getBundle();
  }

  @Override
  public ComponentInstance getComponentInstance() {
    return null;
  }

  @Override
  public void enableComponent(String s) {

  }

  @Override
  public void disableComponent(String s) {

  }

  @Override
  public ServiceReference<?> getServiceReference() {
    return null;
  }

  @Override
  public <S> S locateService(String s, ServiceReference<S> serviceReference) {
    return null;
  }

}
