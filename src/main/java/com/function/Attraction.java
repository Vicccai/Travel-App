package com.function;

import org.json.JSONObject;

public class Attraction {
  private String price;
  private int score;
  private String title;

  public Attraction(String title, String price, int score) {
    this.title = title;
    this.price = price;
    this.score = score;
  }

  public String getTitle() {
    return title;
  }

  public int getScore() {
    return score;
  }

  public String getPrice() {
    return price;
  }

  public static Attraction jsonToAttraction(JSONObject attraction) {
    String price = attraction.getString("fromPriceValue");
    String title = attraction.getString("title");
    int score = attraction.getInt("recommendationScore");
    return new Attraction(title, price, score);
  }

  public static JSONObject attractionToJson(Attraction attraction) {
    JSONObject result = new JSONObject();
    result.put("price", attraction.price);
    result.put("title", attraction.title);
    result.put("recommendationScore", attraction.score);
    return result;
  }

}
