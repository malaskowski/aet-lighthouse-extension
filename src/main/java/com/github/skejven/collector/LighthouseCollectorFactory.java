/*
 * aet-extensions: lighthouse
 *
 * Copyright (C) 2019 Maciej Laskowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.skejven.collector;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Designate(ocd = LighthouseCollectorFactoryConf.class)
public class LighthouseCollectorFactory implements CollectorFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(LighthouseCollectorFactory.class);

  @Reference
  private ArtifactsDAO artifactsDAO;
  private LighthouseCollectorFactoryConf config;
  private PoolingHttpClientConnectionManager poolingConnManager;
  private CloseableHttpClient httpClient;

  @Override
  public String getName() {
    return LighthouseCollector.NAME;
  }

  @Override
  public CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper) throws ParametersException {

    LighthouseCollector collector = new LighthouseCollector(artifactsDAO, properties,
        config.lighthouseInstanceUri(), httpClient);
    collector.setParameters(parameters);
    return collector;
  }

  @Activate
  public void activate(LighthouseCollectorFactoryConf config) {
    this.config = config;
    poolingConnManager = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
    poolingConnManager.setMaxTotal(5);
    httpClient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
  }

  @Deactivate
  protected void deactivate() {
    if (httpClient != null) {
      try {
        httpClient.close();
      } catch (IOException e) {
        LOGGER.error("Can't close httpClient", e);
      }
    }
    if (poolingConnManager != null) {
      poolingConnManager.shutdown();
    }
  }

}
