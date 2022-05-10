package com.function;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AttractionService {
  public static JSONArray lookupAttractions(String location, String startDate,
      String endDate) throws JSONException, IOException {
    String url = "https://www.expedia.com:443/lx/api/search?location=" + location + "&startDate="
        + convertDate(startDate) + "&endDate=" + convertDate(endDate);
    // read json at url
    ReadJson reader = new ReadJson();
    JSONObject json = reader.readJsonFromUrl(url);
    JSONArray attractions;
    try {
      attractions = getAttractions(json);
    } catch (JSONException e) {
      return null;
    }
    return attractions;
  }

  private static JSONArray getAttractions(JSONObject json) {
    JSONArray activities = json.getJSONArray("activities");
    // put activities into Attraction array
    Attraction[] attractions = new Attraction[activities.length()];
    int index = 0;
    for (Object activity : activities) {
      Attraction act = Attraction.jsonToAttraction((JSONObject) activity);
      attractions[index++] = act;
    }
    // sort attractions by recommendation score
    Arrays.sort(attractions, Comparator.comparing(Attraction::getScore));

    // return top 5
    JSONArray result = new JSONArray();
    int len = attractions.length;
    int n = len >= 5 ? 5 : len;
    for (int i = 0; i < n; i++) {
      JSONObject attraction = Attraction.attractionToJson(attractions[len - 1 - i]);
      result.put(attraction);
    }

    return result;
  }

  /** Convert date from format yyyy-mm-dd to mm/dd/yyyy */
  private static String convertDate(String date) {
    String[] elements = date.split("-");
    String result = elements[1] + "/" + elements[2] + "/" + elements[0];
    return result;
  }
}
