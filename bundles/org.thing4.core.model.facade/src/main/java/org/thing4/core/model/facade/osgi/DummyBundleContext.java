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

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Dictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class DummyBundleContext implements BundleContext {

  private DummyBundle bundle;

  public DummyBundleContext(String bundleName) {
     this.bundle = new DummyBundle(this, bundleName);
  }

  @Override
  public String getProperty(String s) {
    return null;
  }

  @Override
  public Bundle getBundle() {
    return bundle;
  }

  @Override
  public Bundle installBundle(String s, InputStream inputStream) throws BundleException {
    return null;
  }

  @Override
  public Bundle installBundle(String s) throws BundleException {
    return null;
  }

  @Override
  public Bundle getBundle(long l) {
    return null;
  }

  @Override
  public Bundle[] getBundles() {
    return new Bundle[] {
        bundle
    };
  }

  @Override
  public void addServiceListener(ServiceListener serviceListener, String s)
      throws InvalidSyntaxException {

  }

  @Override
  public void addServiceListener(ServiceListener serviceListener) {

  }

  @Override
  public void removeServiceListener(ServiceListener serviceListener) {

  }

  @Override
  public void addBundleListener(BundleListener bundleListener) {

  }

  @Override
  public void removeBundleListener(BundleListener bundleListener) {

  }

  @Override
  public void addFrameworkListener(FrameworkListener frameworkListener) {

  }

  @Override
  public void removeFrameworkListener(FrameworkListener frameworkListener) {

  }

  @Override
  public ServiceRegistration<?> registerService(String[] strings, Object o,
      Dictionary<String, ?> dictionary) {
    return null;
  }

  @Override
  public ServiceRegistration<?> registerService(String s, Object o,
      Dictionary<String, ?> dictionary) {
    return null;
  }

  @Override
  public <S> ServiceRegistration<S> registerService(Class<S> aClass, S s,
      Dictionary<String, ?> dictionary) {
    return null;
  }

  @Override
  public <S> ServiceRegistration<S> registerService(Class<S> aClass,
      ServiceFactory<S> serviceFactory, Dictionary<String, ?> dictionary) {
    return null;
  }

  @Override
  public ServiceReference<?>[] getServiceReferences(String s, String s1)
      throws InvalidSyntaxException {
    return new ServiceReference[0];
  }

  @Override
  public ServiceReference<?>[] getAllServiceReferences(String s, String s1)
      throws InvalidSyntaxException {
    return new ServiceReference[0];
  }

  @Override
  public ServiceReference<?> getServiceReference(String s) {
    return null;
  }

  @Override
  public <S> ServiceReference<S> getServiceReference(Class<S> aClass) {
    return null;
  }

  @Override
  public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> aClass, String s)
      throws InvalidSyntaxException {
    return null;
  }

  @Override
  public <S> S getService(ServiceReference<S> serviceReference) {
    return null;
  }

  @Override
  public boolean ungetService(ServiceReference<?> serviceReference) {
    return false;
  }

  @Override
  public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> serviceReference) {
    return null;
  }

  @Override
  public File getDataFile(String s) {
    return null;
  }

  @Override
  public Filter createFilter(String s) throws InvalidSyntaxException {
    return null;
  }

  @Override
  public Bundle getBundle(String s) {
    return null;
  }
}
