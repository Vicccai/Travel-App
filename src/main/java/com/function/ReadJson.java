package com.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

class ReadJson {
  public JSONObject readJsonFromUrl(String link) throws IOException, JSONException {
    InputStream input = new URL(link).openStream();
    // Input Stream Object To Start Streaming.
    try { // try catch for checked exception
      BufferedReader re = new BufferedReader(
          new InputStreamReader(input, Charset.forName("UTF-8")));
      // Buffer Reading In UTF-8
      String Text = Read(re); // Handy Method To Read Data From BufferReader
      JSONObject json = new JSONObject(Text); // Creating A JSON
      return json; // Returning JSON
    } catch (Exception e) {
      return null;
    } finally {
      input.close();
    }
  }

  public String Read(Reader re) throws IOException { // class Declaration
    StringBuilder str = new StringBuilder(); // To Store Url Data In String.
    int temp;
    do {

      temp = re.read(); // reading Charcter By Chracter.
      str.append((char) temp);

    } while (temp != -1);
    // re.read() return -1 when there is end of buffer , data or end of file.

    return str.toString();

  }

}