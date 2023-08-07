# Travel-App
This travel app allows users to input parameters including budget, preferred location type (tropical, hiking, skiing), start and end date, number of travelers, and departure location. The return is a list of vacations that fit into these parameters, including information on hotels, flights and attractions.

This repository contains the flight and attraction service, utilizing API calls to Expedia. The services can be deployed on Azure cloud. The services use multi-threading to retrieve information on multiple locations in parallel, and CosmosDB to cache flight and attraction data for certain locations to reduce latency.

Here is the repository for the hotel service: https://github.com/AWHochman/HotelService
And here is the repository for the main computation service that compiles the data from all of the microservices: https://github.com/AWHochman/ComputationService
