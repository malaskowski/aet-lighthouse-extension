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
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.github.skejven.comparator.score.LighthouseScores;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.Map;

public class LighthouseComparator implements ComparatorJob {

  public static final String TYPE = "lighthouse";

  public static final String NAME = "lighthouse";

  private final ArtifactsDAO artifactsDAO;
  private final ComparatorProperties properties;
  private final LighthouseReportAnalyser analyser;
  private final JsonParser jsonParser;


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
      LighthouseScores patternScores = getSimplifiedReport(jsonParser, properties.getPatternId());
      LighthouseScores currentScores = getSimplifiedReport(jsonParser, properties.getCollectedId());

      LighthouseComparisonResult comparisonResult = analyser.compare(patternScores, currentScores);

      String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, comparisonResult);
      result = getPassedStepResult(artifactId);
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    //no parameters needed
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

  private ComparatorStepResult getPassedStepResult(String artifactId) {
    ComparatorStepResult result = new ComparatorStepResult(artifactId,
        ComparatorStepResult.Status.PASSED, false);
    addTimestampToResult(result);
    return result;
  }
}
