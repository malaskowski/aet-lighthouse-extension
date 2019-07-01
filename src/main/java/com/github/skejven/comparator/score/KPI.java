package com.github.skejven.comparator.score;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KPI {

  private static final Logger LOGGER = LoggerFactory.getLogger(KPI.class);
  private static final int DEFAULT_KPI_VALUE = 75;
  private static final int DEFAULT_THRESHOLD_VALUE = 5;

  private int performance;
  private int accessibility;
  private int bestPractices;
  private int seo;
  private int threshold;

  public int getPerformance() {
    return performance;
  }

  public int getAccessibility() {
    return accessibility;
  }

  public int getBestPractices() {
    return bestPractices;
  }

  public int getSeo() {
    return seo;
  }

  public int getThreshold() {
    return threshold;
  }

  @Override
  public String toString() {
    return "KPI{" +
        "performance=" + performance +
        ", accessibility=" + accessibility +
        ", bestPractices=" + bestPractices +
        ", seo=" + seo +
        ", threshold=" + threshold +
        '}';
  }

  public static final class Builder {

    private int performance;
    private int accessibility;
    private int bestPractices;
    private int seo;
    private int threshold;

    private Builder() {
    }

    public static Builder aKPI() {
      return new Builder();
    }

    public Builder withPerformance(String performance) {
      this.performance = getOrDefault(performance, DEFAULT_KPI_VALUE);
      return this;
    }

    public Builder withAccessibility(String accessibility) {
      this.accessibility = getOrDefault(accessibility, DEFAULT_KPI_VALUE);
      return this;
    }

    public Builder withBestPractices(String bestPractices) {
      this.bestPractices = getOrDefault(bestPractices, DEFAULT_KPI_VALUE);
      return this;
    }

    public Builder withSeo(String seo) {
      this.seo = getOrDefault(seo, DEFAULT_KPI_VALUE);
      return this;
    }

    public Builder withThreshold(String threshold) {
      this.threshold = getOrDefault(threshold, DEFAULT_THRESHOLD_VALUE);
      return this;
    }

    public KPI build() {
      KPI kPI = new KPI();
      kPI.performance = this.performance;
      kPI.accessibility = this.accessibility;
      kPI.seo = this.seo;
      kPI.threshold = this.threshold;
      kPI.bestPractices = this.bestPractices;
      return kPI;
    }

    private int getOrDefault(String paramValue, int defaultValue) {
      int result = defaultValue;
      if (StringUtils.isNotBlank(paramValue)) {
        try {
          result = Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
          LOGGER.warn("Invalid parameter {} value: {}", paramValue, e);
        }
      }
      return result;
    }
  }
}
