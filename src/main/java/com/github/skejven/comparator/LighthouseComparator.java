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
package com.github.skejven.comparator;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.github.skejven.comparator.score.KPI;
import com.github.skejven.comparator.score.LighthouseScores;
import com.github.skejven.comparator.score.LighthouseScoresDiff;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LighthouseComparator implements ComparatorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(LighthouseComparator.class);

  static final String TYPE = "lighthouse";
  static final String NAME = "lighthouse";

  private final ArtifactsDAO artifactsDAO;
  private final ComparatorProperties properties;
  private final LighthouseReportAnalyser analyser;
  private final JsonParser jsonParser;
  private KPI kpi;

  LighthouseComparator(ArtifactsDAO artifactsDAO, ComparatorProperties comparatorProperties,
      LighthouseReportAnalyser analyser, JsonParser jsonParser) {
    this.artifactsDAO = artifactsDAO;
    this.properties = comparatorProperties;
    this.analyser = analyser;
    this.jsonParser = jsonParser;
  }

  @Override
  public ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;
    try {
      LighthouseScores currentScores = getSimplifiedReport(jsonParser, properties.getCollectedId());

      LighthouseComparisonResult comparisonResult = analyser.compare(currentScores, kpi);

      String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, comparisonResult);
      result = getStepResult(comparisonResult.getDiff(), artifactId);
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) {
    kpi = KPI.Builder.aKPI()
        .withPerformance(params.get("kpi-performance"))
        .withAccessibility(params.get("kpi-accessibility"))
        .withBestPractices(params.get("kpi-best-practices"))
        .withSeo(params.get("kpi-seo"))
        .withThreshold(params.get("kpi-threshold"))
        .build();
    LOGGER.debug("KPI for LighthouseComparator: {}", kpi);
  }

  private LighthouseScores getSimplifiedReport(JsonParser jsonParser, String collectedId)
      throws IOException {
    String collectedArtifact = artifactsDAO
        .getArtifactAsString(properties, collectedId);
    JsonObject current = jsonParser.parse(collectedArtifact).getAsJsonObject();
    return new LighthouseScores(current);
  }

  private void addTimestampToResult(ComparatorStepResult result) {
    result.addData("patternTimestamp", Long.toString(
        artifactsDAO.getArtifactUploadDate(properties, properties.getPatternId()).getTime()));
    result.addData("collectTimestamp", Long.toString(System.currentTimeMillis()));
  }

  private ComparatorStepResult getStepResult(LighthouseScoresDiff diff, String artifactId) {
    Status status = diff.allPassed() ? Status.PASSED : Status.FAILED;
    ComparatorStepResult result = new ComparatorStepResult(artifactId, status, false);
    addTimestampToResult(result);
    return result;
  }
}
