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

class LighthouseComparisonResult {

  private KPI kpi;
  private LighthouseScores current;
  private LighthouseScoresDiff diff;

  LighthouseComparisonResult(
      KPI kpi,
      LighthouseScores current,
      LighthouseScoresDiff diff) {
    this.kpi = kpi;
    this.current = current;
    this.diff = diff;
  }

  public KPI getKpi() {
    return kpi;
  }

  public LighthouseScores getCurrent() {
    return current;
  }

  public LighthouseScoresDiff getDiff() {
    return diff;
  }
}
