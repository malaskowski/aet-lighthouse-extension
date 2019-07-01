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

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LighthouseCollector implements CollectorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(LighthouseCollector.class);

  public static final String NAME = "lighthouse";

  private final ArtifactsDAO artifactsDAO;
  private final CollectorProperties collectorProperties;
  private final String lighthouseInstanceUri;

  LighthouseCollector(ArtifactsDAO artifactsDAO,
      CollectorProperties collectorProperties, String lighthouseInstanceUri) {
    this.artifactsDAO = artifactsDAO;
    this.collectorProperties = collectorProperties;
    this.lighthouseInstanceUri = lighthouseInstanceUri;
  }

  @Override
  public final CollectorStepResult collect() throws ProcessingException {
    final CollectorStepResult stepResult;
    try {
      String report = getLighthouseReportFor(collectorProperties.getUrl());
      LOGGER.debug("Got report from lighthouse: {}", report);
      String resultId = artifactsDAO.saveArtifactInJsonFormat(
          collectorProperties, new JsonParser().parse(report).getAsJsonObject()
      );
      stepResult = CollectorStepResult.newCollectedResult(resultId);
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return stepResult;
  }

  private String getLighthouseReportFor(String url) throws IOException {
    LOGGER.debug("Collecting report from {} for {}", lighthouseInstanceUri, url);
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("output", "json");
    requestBody.addProperty("url", url);
    Request lighthouseRequest = Request.Post(lighthouseInstanceUri)
        .connectTimeout(30000)
        .socketTimeout(30000)
        .addHeader("X-API-KEY", "AET")
        .addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
        .bodyString(requestBody.toString(), ContentType.APPLICATION_JSON);

    return lighthouseRequest.execute().returnContent().asString(Charset.forName("UTF-8"));
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
  }
}
