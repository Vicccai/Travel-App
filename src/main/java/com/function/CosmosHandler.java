package com.function;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;

import java.time.Duration;

import org.json.JSONObject;

import com.azure.cosmos.models.CosmosContainerProperties;

import java.time.Instant;
import java.util.Collections;

public class CosmosHandler {
  private CosmosContainer container;

  public CosmosHandler() {
    CosmosClient client = new CosmosClientBuilder()
        .endpoint(AccountSettings.HOST)
        .key(AccountSettings.MASTER_KEY)
        // Setting the preferred location to Cosmos DB Account region
        // West US is just an example. User should set preferred location to the Cosmos
        // DB region closest to the application
        .preferredRegions(Collections.singletonList("East US"))
        .consistencyLevel(ConsistencyLevel.EVENTUAL)
        .buildClient();
    CosmosDatabaseResponse cosmosDatabaseResponse = client.createDatabaseIfNotExists("Travel");
    CosmosDatabase database = client.getDatabase(cosmosDatabaseResponse.getProperties().getId());
    CosmosContainerProperties containerProperties = new CosmosContainerProperties("Flights", "/id");
    CosmosContainerResponse cosmosContainerResponse = database.createContainerIfNotExists(containerProperties);
    container = database.getContainer(cosmosContainerResponse.getProperties().getId());
  }

  public void createItem(String id, JSONObject flight) throws Exception {
    String time = Instant.now().toString();
    FlightResponseObject item = new FlightResponseObject(id, time, flight.toString());
    container.createItem(item, new PartitionKey(id), new CosmosItemRequestOptions());
  }

  public void replaceItem(String id, JSONObject flight) throws Exception {
    String time = Instant.now().toString();
    FlightResponseObject item = new FlightResponseObject(id, time, flight.toString());
    container.replaceItem(item, id, new PartitionKey(id), new CosmosItemRequestOptions());
  }

  /**
   * Tries to get flight object by id in Cosmos db, returns null if not found.
   * If not within 24 hrs of last put, then returns null.
   * 
   * @param id
   * @return
   */
  public JSONObject getFlightById(String id) {
    try {
      CosmosItemResponse<FlightResponseObject> item = container.readItem(id,
          new PartitionKey(id),
          FlightResponseObject.class);
      String timeOfItem = item.getItem().getTime();
      String flight = item.getItem().getFlight();
      if (isWithinDay(timeOfItem, Instant.now())) {
        return new JSONObject(flight);
      } else {
        return null;
      }
    } catch (CosmosException e) {
      return null;
    }
  }

  private boolean isWithinDay(String time1, Instant instant2) {
    Instant instant1 = Instant.parse(time1);
    Duration dur = Duration.between(instant1, instant2);
    return dur.toDaysPart() == 0;
  }
}
