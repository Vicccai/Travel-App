package com.function;

import org.json.*;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.IOException;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
     * using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("QueryFlights")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET,
                    HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String departAirport = request.getBody().orElse(request.getQueryParameters().get("departAirport"));
        // final String arrivalAirport =
        // request.getBody().orElse(request.getQueryParameters().get("arrivalAirport"));
        final String departDate = request.getBody().orElse(request.getQueryParameters().get("departDate"));
        final String returnDate = request.getBody().orElse(request.getQueryParameters().get("returnDate"));
        final String numTravelers = request.getBody().orElse(request.getQueryParameters().get("numTravelers"));
        final String preference = request.getBody().orElse(request.getQueryParameters().get("preference"));
        final String list = request.getBody().orElse(request.getQueryParameters().get("list"));
        final String exclude = request.getBody().orElse(request.getQueryParameters().get("exclude"));
        String[] excludeList = FlightService.parseExclude(exclude);

        String[] airports = FlightService.getAirports(preference);
        int n = airports.length;
        JSONObject[] result = new JSONObject[n];
        // threading
        Thread[] threads = new Thread[n];
        try {
            for (int i = 0; i < n; i++) {
                Thread T = new Thread(new FlightService(i, departAirport, airports[i],
                        departDate, returnDate, Integer.parseInt(numTravelers), result, excludeList));
                threads[i] = T;
                T.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            if (list.equals("true")) {
                JSONArray arr = new JSONArray();
                for (JSONObject flight : result) {
                    if (flight != null) {
                        arr.put(flight);
                    }
                }
                return request.createResponseBuilder(HttpStatus.OK).body(arr.toString(3)).build();
            } else {
                JSONObject cheapest = FlightService.findCheapest(result);
                return request.createResponseBuilder(HttpStatus.OK).body(cheapest.toString(3)).build();
            }

        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid parameters").build();
        }

        // if (name == null) {
        // return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
        // .body("Please pass a name on the query string or in the request
        // body").build();
        // } else {
        // return
        // request.createResponseBuilder(HttpStatus.OK).body(result.toString()).build();
        // }
    }
}
