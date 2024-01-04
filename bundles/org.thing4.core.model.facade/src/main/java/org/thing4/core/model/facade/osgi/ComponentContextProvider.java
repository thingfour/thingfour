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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class ComponentContextProvider implements Provider<ComponentContext> {

  private Provider<BundleContext> bundleContext;

  @Inject
  public ComponentContextProvider(Provider<BundleContext> bundleContext) {
    this.bundleContext = bundleContext;
  }

  @Override
  public ComponentContext get() {
    return new DummyComponentContext(bundleContext.get());
  }

}
