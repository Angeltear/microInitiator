# MicroInitiator
Producer microservice for [MicroThrottler](https://github.com/Angeltear/microThrottler). Its purpose is to produce the elements of a redis queue, based on endpoint input.

## Both services are part of a project that needs the following functionality:
 1. Application 1 that receives multiple concurent transactions trough an API endpoint
 2. Application 1 transferring data to Application 2 to be processed
 3. Application 2 to implement a throttling mechanism to reduce the load on the database

# Processing logic
1. Upon application startup, the service start accepting requests on the "/" endpoint.
2. Establishing connection to redis and check the length of a queue if it's full (capacity is controlled trough a property). If the queue is full, return 429 - TOO_MANY_REQUESTS.
3. Check client's custom queue and reject connection with 429 - TOO_MANY_REQUESTS if it's full.
4. If both queues aren't full, push the element, received in the request to the main queue, as well as add an element to the client's personal queue.

# Startup configuration
1. For dev/demo purposes, all endpoints (Redis, Postgres, Application) run on localhost with their default ports. The application (this microservice) is an exception - it runs on port 8090, because of potential conflicts on 8080. Endpoints are controlled trough properties.
2. Run the Redis server and Postgres instances on localhost.
3. Start the service

# Additional info
1. This application is integrated with OpenAPI 3.0 descriptor (Swagger). Apart from  exploring the classes and endpoints, the [SwaggerUI](http://localhost:8090/swagger-ui/index.html) can be used to make requests to the service.
2. Sample request body:
   ```
   {
     "clientId": 1,
     "paymentId": 13,
     "paymentSum": 1.53
   }
   ```

3. Test method "testConsumer()", as the name suggests, can be used to test the consumer (MicroThrottler) by bulk adding elements to the queue, load testing the consumer.
