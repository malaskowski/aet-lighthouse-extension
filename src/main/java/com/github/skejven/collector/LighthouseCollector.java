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
import com.google.common.io.CharStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LighthouseCollector implements CollectorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(LighthouseCollector.class);

  static final String NAME = "lighthouse";

  private final ArtifactsDAO artifactsDAO;
  private final CollectorProperties collectorProperties;
  private final String lighthouseInstanceUri;
  private final CloseableHttpClient httpClient;

  LighthouseCollector(ArtifactsDAO artifactsDAO,
      CollectorProperties collectorProperties, String lighthouseInstanceUri,
      CloseableHttpClient httpClient) {
    this.artifactsDAO = artifactsDAO;
    this.collectorProperties = collectorProperties;
    this.lighthouseInstanceUri = lighthouseInstanceUri;
    this.httpClient = httpClient;
  }

  @Override
  public final CollectorStepResult collect() throws ProcessingException {
    String result = getLighthouseReportFor(collectorProperties.getUrl());
    LOGGER.debug("Got report from lighthouse: {}", result);
    JsonObject resultAsJson = new JsonParser().parse(result).getAsJsonObject();
    String resultId = artifactsDAO.saveArtifactInJsonFormat(
        collectorProperties, resultAsJson.getAsJsonObject("report")
    );
    return CollectorStepResult.newCollectedResult(resultId);
  }

  private String getLighthouseReportFor(String url) throws ProcessingException {
    LOGGER.debug("Collecting report from {} for {}", lighthouseInstanceUri, url);
    JsonObject requestBody = new JsonObject();
    requestBody.addProperty("output", "json");
    requestBody.addProperty("url", url);

    HttpPost request = new HttpPost(lighthouseInstanceUri);
    request.setEntity(new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON));
    request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

    String result;
    try (CloseableHttpResponse response = httpClient.execute(request)) {
      final int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        result = CharStreams
            .toString(new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8));
        if (LOGGER.isTraceEnabled()) {
          LOGGER.debug("Response [{}] {} result: {}", statusCode, request.getURI(), result);
        }
      } else {
        throw new ProcessingException(String
            .format("Couldn't get Lighthouse report from %s : status code: %d",
                request.getURI().toString(), statusCode));
      }
    } catch (IOException e) {
      throw new ProcessingException(
          String.format("Couldn't connect to %s", request.getURI().toString()), e);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
  }
}
