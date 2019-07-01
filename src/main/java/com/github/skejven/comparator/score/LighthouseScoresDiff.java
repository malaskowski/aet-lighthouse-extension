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
package com.github.skejven.comparator.score;

import java.util.stream.Stream;

public class LighthouseScoresDiff {

  private ScoreDiff performanceDiff;
  private ScoreDiff accessibilityDiff;
  private ScoreDiff bestPracticesDiff;
  private ScoreDiff seoDiff;

  public boolean allPassed() {
    return Stream.of(performanceDiff, accessibilityDiff, bestPracticesDiff, seoDiff)
        .noneMatch(s -> Status.FAIL == s.getStatus());
  }

  static class ScoreDiff {

    private double diff;
    private Status status;

    ScoreDiff(double diff, double threshold) {
      this.diff = diff;
      this.status = Status.fromDiff(diff, threshold);
    }

    public double getDiff() {
      return diff;
    }

    public Status getStatus() {
      return status;
    }
  }

  private enum Status {
    PASS,
    FAIL;

    static Status fromDiff(double diff, double threshold) {
      if (diff < 0 && threshold < Math.abs(diff)) {
        return FAIL;
      } else {
        return PASS;
      }
    }
  }

  public static final class Builder {

    private ScoreDiff performanceDiff;
    private ScoreDiff accessibilityDiff;
    private ScoreDiff bestPracticesDiff;
    private ScoreDiff seoDiff;

    private final double threshold;

    private Builder(double threshold) {
      this.threshold = threshold;
    }

    public static Builder aLighthouseScoresDiff(double thershold) {
      return new Builder(thershold);
    }

    public Builder withPerformanceDiff(double performanceDiff) {
      this.performanceDiff = new ScoreDiff(performanceDiff, threshold);
      return this;
    }

    public Builder withAccessibilityDiff(double accessibilityDiff) {
      this.accessibilityDiff = new ScoreDiff(accessibilityDiff, threshold);
      return this;
    }

    public Builder withBestPracticesDiff(double bestPracticesDiff) {
      this.bestPracticesDiff = new ScoreDiff(bestPracticesDiff, threshold);
      return this;
    }

    public Builder withSeoDiff(double seoDiff) {
      this.seoDiff = new ScoreDiff(seoDiff, threshold);
      return this;
    }

    public LighthouseScoresDiff build() {
      LighthouseScoresDiff lighthouseScoresDiff = new LighthouseScoresDiff();
      lighthouseScoresDiff.seoDiff = this.seoDiff;
      lighthouseScoresDiff.bestPracticesDiff = this.bestPracticesDiff;
      lighthouseScoresDiff.performanceDiff = this.performanceDiff;
      lighthouseScoresDiff.accessibilityDiff = this.accessibilityDiff;
      return lighthouseScoresDiff;
    }
  }
}
