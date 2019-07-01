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

import com.github.skejven.comparator.score.KPI;
import com.github.skejven.comparator.score.LighthouseScores;
import com.github.skejven.comparator.score.LighthouseScoresDiff;
import com.github.skejven.comparator.score.LighthouseScoresDiff.Builder;

class LighthouseReportAnalyser {

  LighthouseComparisonResult compare(LighthouseScores current, KPI kpi) {
    LighthouseScoresDiff diff = Builder.aLighthouseScoresDiff(toPercentage(kpi.getThreshold()))
        .withPerformanceDiff(diff(current.getPerformance(), kpi.getPerformance()))
        .withAccessibilityDiff(diff(current.getAccessibility(), kpi.getAccessibility()))
        .withBestPracticesDiff(diff(current.getBestPractices(), kpi.getBestPractices()))
        .withSeoDiff(diff(current.getSeo(), kpi.getSeo()))
        .build();

    return new LighthouseComparisonResult(kpi, current, diff);
  }

  private double diff(double current, int kpi) {
    return current - toPercentage(kpi);
  }

  private double toPercentage(int i) {
    return i / 100.d;
  }

}