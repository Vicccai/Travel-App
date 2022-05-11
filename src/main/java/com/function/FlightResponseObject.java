package com.function;

import org.json.JSONObject;

public class FlightResponseObject {
  private String id;
  private String time;
  private String flight;

  public FlightResponseObject() {
  }

  public FlightResponseObject(String id, String time, String flight) {
    this.id = id;
    this.time = time;
    this.flight = flight;
  }

  public String getFlight() {
    return flight;
  }

  public String getTime() {
    return time;
  }

  public String getId() {
    return id;
  }

}
