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

import com.google.gson.JsonObject;

public class LighthouseScores {

  private double performance;
  private double accessibility;
  private double bestPractices;
  private double seo;

  public LighthouseScores(JsonObject jsonObject) {
    JsonObject categories = jsonObject.getAsJsonObject("categories");
    this.performance = getScore(categories, "performance");
    this.accessibility = getScore(categories, "accessibility");
    this.bestPractices = getScore(categories, "best-practices");
    this.seo = getScore(categories, "seo");
  }

  public double getPerformance() {
    return performance;
  }

  public double getAccessibility() {
    return accessibility;
  }

  public double getBestPractices() {
    return bestPractices;
  }

  public double getSeo() {
    return seo;
  }

  JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("performance", performance);
    json.addProperty("accessibility", accessibility);
    json.addProperty("bestPractices", bestPractices);
    json.addProperty("seo", seo);
    return json;
  }

  @Override
  public String toString() {
    return toJson().toString();
  }

  private double getScore(JsonObject categories, String key) {
    return categories.getAsJsonObject(key).get("score").getAsDouble();
  }
}