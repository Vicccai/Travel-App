package com.function;

import java.io.IOException;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FlightService implements Runnable {
  static String[] tropical = { "MLE", "PPT", "CUN", "SJO", "PCC", "SDQ", "HNL", "AUA", "FPO",
      "NAN" };
  // static String[] tropical= { "MLE", "PPT", "CUN" };
  static String[] major = { "MEX", "HND", "CDG", "BCN", "FCO", "LGA", "LHR", "SYD", "YYZ", "BER" };
  static String[] hiking = { "FAT", "FCA", "SLC", "LAS", "PHX", "JAC", "YYC", "RNO", "CHO",
      "SFO" };
  private int index;
  private String depart;
  private String arrive;
  private String departDate;
  private String returnDate;
  private int numTravelers;
  private JSONObject[] result;
  private String[] exclude;

  public FlightService() {

  }

  public FlightService(int index, String depart, String arrive, String departDate,
      String returnDate, int numTravelers, JSONObject[] result, String[] exclude) {
    this.index = index;
    this.result = result;
    this.depart = depart;
    this.arrive = arrive;
    this.departDate = departDate;
    this.returnDate = returnDate;
    this.numTravelers = numTravelers;
    this.exclude = exclude;
  }

  @Override
  public void run() {
    try {
      if (Arrays.asList(exclude).contains(arrive)) {
        result[index] = null;
      } else {
        JSONObject obj = lookupFlights(depart, arrive, departDate, returnDate, numTravelers);
        result[index] = obj;
      }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Returns JSONObject of cheapest round trip flight based on input parameters.
   *
   * @param departureAirport departure airport code
   * @param arrivalAirport   arrival airport code
   * @param departureDate    departure date in "yyyy-mm-dd" format
   * @param returnDate       return date in "yyyy-mm-dd" format
   * @param numTravelers     number of travelers
   * @throws IOException
   * @throws JSONException
   */
  public JSONObject lookupFlights(String departureAirport, String arrivalAirport,
      String departureDate, String returnDate, int numTravelers)
      throws JSONException, IOException {
    String url = "https://www.expedia.com/api/flight/search?departureDate=" +
        departureDate + "&returnDate=" + returnDate + "&departureAirport=" +
        departureAirport + "&arrivalAirport=" + arrivalAirport +
        "&numberOfAdultTravelers=" + Integer.toString(numTravelers) + "&maxOfferCount=1";
    // read json at url
    ReadJson reader = new ReadJson();
    JSONObject json = reader.readJsonFromUrl(url);
    JSONObject flight;
    try {
      flight = getFlights(json);
    } catch (JSONException e) {
      return null;
    }
    return flight;
  }

  public static JSONObject findCheapest(JSONObject[] flights) {
    double minPrice = Integer.MAX_VALUE;
    JSONObject minFlight = null;
    for (JSONObject flight : flights) {
      if (flight == null)
        continue;
      String stringPrice = (String) flight.get("TotalRoundTripPrice");
      double price = Double.parseDouble(stringPrice);
      if (price < minPrice) {
        minPrice = price;
        minFlight = flight;
      }
    }
    return minFlight;
  }

  public static String[] getAirports(String preference) {
    switch (preference) {
      case "tropical":
        return tropical;
      case "major":
        return major;
      case "hiking":
        return hiking;
      default:
        return null;
    }
  }

  private JSONObject getFlights(JSONObject json) {
    JSONObject result = new JSONObject();
    String price = getPrice(json);
    result.accumulate("TotalRoundTripPrice", price);

    getOutbound(json, result);
    getReturn(json, result);

    return result;
  }

  private void getOutbound(JSONObject json, JSONObject result) {
    JSONArray legs = json.getJSONArray("legs");
    JSONObject departLeg = (JSONObject) legs.get(0);
    JSONArray departLegSegmentArray = departLeg.getJSONArray("segments");
    getFlightHelper(result, departLegSegmentArray, "outboundTrip");
  }

  private void getReturn(JSONObject json, JSONObject result) {
    JSONArray legs = json.getJSONArray("legs");
    JSONObject departLeg = (JSONObject) legs.get(1);
    JSONArray departLegSegmentArray = departLeg.getJSONArray("segments");
    getFlightHelper(result, departLegSegmentArray, "returnTrip");
  }

  private void getFlightHelper(JSONObject result, JSONArray departLegSegmentArray, String key) {
    int legNumber = 1;
    for (Object departSegment : departLegSegmentArray) {
      JSONObject departLegSegment = (JSONObject) departSegment;
      // leg number
      JSONObject flight = new JSONObject();
      flight.accumulate("legNumber", legNumber);
      // airline
      String airline = departLegSegment.getString("airlineName");
      flight.accumulate("airline", airline);

      // departure
      String departTime = departLegSegment.getString("departureTime");
      String departAirportCode = departLegSegment.getString("departureAirportCode");
      String departAirportName = departLegSegment.getString("departureAirportName");
      String departFrom = departLegSegment.getString("departureAirportLocation");

      JSONObject departure = new JSONObject();
      departure.accumulate("departTime", departTime);
      departure.accumulate("departFrom", departFrom);
      departure.accumulate("departAirportCode", departAirportCode);
      departure.accumulate("departAirportName", departAirportName);
      flight.accumulate("departure", departure);

      // arrival
      String arrivalTime = departLegSegment.getString("arrivalTime");
      String arrivalAirportCode = departLegSegment.getString("arrivalAirportCode");
      String arrivalAirportName = departLegSegment.getString("arrivalAirportName");
      String arriveAt = departLegSegment.getString("arrivalAirportLocation");

      JSONObject arrival = new JSONObject();
      arrival.accumulate("arrivalTime", arrivalTime);
      arrival.accumulate("arriveAt", arriveAt);
      arrival.accumulate("arrivalAirportCode", arrivalAirportCode);
      arrival.accumulate("arrivalAirportName", arrivalAirportName);
      flight.accumulate("arrival", arrival);
      result.append(key, flight);
      legNumber++;
    }
  }

  private String getPrice(JSONObject json) {
    JSONObject offer = (JSONObject) json.getJSONArray("offers").get(0);
    String price = offer.getString("totalFare");
    return price;
  }

  /**
   * Converts exclude to a String list of airport codes.
   * 
   * @exclude is in the form of [ABC,DEF]
   */
  public static String[] parseExclude(String exclude) {
    String airports = exclude.substring(1, exclude.length() - 1);
    return airports.split(",");
  }

}
