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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

public class DummyBundle implements Bundle {

  private final BundleContext bundleContext;
  private final String bundleName;

  public DummyBundle(BundleContext bundleContext, String bundleName) {
    this.bundleContext = bundleContext;
    this.bundleName = bundleName;
  }

  @Override
  public int getState() {
    return 0;
  }

  @Override
  public void start(int i) throws BundleException {

  }

  @Override
  public void start() throws BundleException {

  }

  @Override
  public void stop(int i) throws BundleException {

  }

  @Override
  public void stop() throws BundleException {

  }

  @Override
  public void update(InputStream inputStream) throws BundleException {

  }

  @Override
  public void update() throws BundleException {

  }

  @Override
  public void uninstall() throws BundleException {

  }

  @Override
  public Dictionary<String, String> getHeaders() {
    return null;
  }

  @Override
  public long getBundleId() {
    return 0;
  }

  @Override
  public String getLocation() {
    return null;
  }

  @Override
  public ServiceReference<?>[] getRegisteredServices() {
    return new ServiceReference[0];
  }

  @Override
  public ServiceReference<?>[] getServicesInUse() {
    return new ServiceReference[0];
  }

  @Override
  public boolean hasPermission(Object o) {
    return false;
  }

  @Override
  public URL getResource(String s) {
    return null;
  }

  @Override
  public Dictionary<String, String> getHeaders(String s) {
    return null;
  }

  @Override
  public String getSymbolicName() {
    return bundleName;
  }

  @Override
  public Class<?> loadClass(String s) throws ClassNotFoundException {
    return null;
  }

  @Override
  public Enumeration<URL> getResources(String s) throws IOException {
    return null;
  }

  @Override
  public Enumeration<String> getEntryPaths(String s) {
    return null;
  }

  @Override
  public URL getEntry(String s) {
    return null;
  }

  @Override
  public long getLastModified() {
    return 0;
  }

  @Override
  public Enumeration<URL> findEntries(String s, String s1, boolean b) {
    return null;
  }

  @Override
  public BundleContext getBundleContext() {
    return bundleContext;
  }

  @Override
  public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int i) {
    return null;
  }

  @Override
  public Version getVersion() {
    return null;
  }

  @Override
  public <A> A adapt(Class<A> aClass) {
    return null;
  }

  @Override
  public File getDataFile(String s) {
    return null;
  }

  @Override
  public int compareTo(Bundle o) {
    return 0;
  }
}
