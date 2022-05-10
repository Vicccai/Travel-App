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

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class AttractionFunction {
  /**
   * This function listens at endpoint "/api/queryflights". Two ways to invoke it
   * using "curl" command in bash:
   * 1. curl -d "HTTP Body" {your host}/api/queryflights
   * 2. curl "{your host}/api/queryflights?name=HTTP%20Query"
   */
  @FunctionName("QueryAttractions")
  public HttpResponseMessage run(
      @HttpTrigger(name = "req", methods = { HttpMethod.GET,
          HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    context.getLogger().info("Java HTTP trigger processed a request.");

    // Parse query parameter
    final String location = request.getBody().orElse(request.getQueryParameters().get("location"));
    final String startDate = request.getBody().orElse(request.getQueryParameters().get("startDate"));
    final String endDate = request.getBody().orElse(request.getQueryParameters().get("endDate"));

    try {
      JSONArray result = AttractionService.lookupAttractions(location, startDate, endDate);
      return request.createResponseBuilder(HttpStatus.OK).body(result.toString(3)).build();
    } catch (Exception e) {
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Invalid parameters").build();
    }
  }
}
