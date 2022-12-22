# Tournament(Tennis) Service

<hr>

This Tournament (tennis) app is a REST service for managing tennis tournament(can be extended in future for others) and retrieving customer licensed matches details

<hr>

### 1.1 Create Tournament

This endpoint creates tournament with match details which can later be subscribed by customers. This endpoint should be accessible by a user with admin access as it can be used for managing tournament details

*POST /tournament*

Sample request and response body

#### Request Body

```
{
    "tournamentType": "TENNIS",
    "startDateTime": "2022-12-21T16:37:38.533+01:00",
    "durationInDays": 3,
    "match": [
        {
            "startDateTime": "2022-12-21T16:37:38.533+01:00",
            "playerA": "A",
            "playerB": "B",
            "durationInMinutes": 2
        }
    ]
}
```
#### Request Body
```
{
    "tournamentId": "7b7bd34d-9a41-4e04-97f9-ea50c1f5b297",
    "tournamentType": "TENNIS",
    "startDateTime": "2022-12-21T15:37:38.533Z",
    "duration": 3,
    "match": [
        {
            "matchId": "10c43d71-5657-4ff4-a242-9a262d53e2b2",
            "startDateTime": "2022-12-21T21:07:38.533+05:30",
            "durationInMinutes": 2,
            "playerA": "A",
            "playerB": "B",
            "summary": null
        }
    ]
}
```
### 1.2 Get Tournament Details

This endpoint is used for retrieving tournament details along with matches, This can be later used by customers for subscribing the matches

*GET /tournament/{tournamentId}*

Sample response

```
{
    "tournamentId": "4394e787-cf09-4129-b473-a2dc2b804f2f",
    "tournamentType": "TENNIS",
    "startDateTime": "2022-12-21T19:07:38.533Z",
    "duration": 3,
    "match": [
        {
            "matchId": "6c33e2e8-d279-4221-82c8-7fc5cc142d6b",
            "startDateTime": "2022-12-22T00:37:38.533+05:30",
            "durationInMinutes": 2,
            "playerA": "A",
            "playerB": "B",
            "summary": null
        }
    ]
}
```
### 1.3 Create Customers

This endpoint is for creating customers

*POST /customer*

Sample request and response body below

#### request body

```
{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2022-12-20"
}
```

#### response body

```
{
    "customerId": "6175b432-e266-41ba-bdbf-604913364599",
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "2022-12-20"
}
```

### 1.4 Update customer license

This endpoint is used for obtaining license to a set of matches

*PATCH /customer/{customerId}/matches*

Sample request body with the list of matchIds (can be obtained from 1.2 Get Tourament details endpoint)

```
{
    "data": [
        "10c43d71-5657-4ff4-a242-9a262d53e2b2"
    ]
}
```

response body

```
{
    "licenseId": "b477abe5-9b99-4093-8d81-523ee7410efc",
    "data": [
        "10c43d71-5657-4ff4-a242-9a262d53e2b2"
    ]
}
```

### 1.5 Get Customer Licensed Matches

This endpoint is used for retrieving the customer licensed matches, This comes with a summary which contains details about the player and can be configured by a optional query parameter(summary)

*GET /customer/{customerId}/licensed/matches*

Sample response

```
{
    "meta": {
        "size": 1,
        "page": 0,
        "next": null
    },
    "data": [
        {
            "matchId": "10c43d71-5657-4ff4-a242-9a262d53e2b2",
            "startDateTime": "2022-12-22T02:37:38.533+05:30",
            "durationInMinutes": 2,
            "playerA": "A",
            "playerB": "B",
            "summary": "A vs B starts in 421 minutes"
        }
    ]
}
```

<hr>

## 2. Technical Details:

### 2.1 Tools&Framework:

The below are the list of tools and framework used in the project!

* [SpringReactive](https://spring.io/reactive) - The framework used
* [Maven](https://maven.apache.org/) - for Dependency Management
* [Java](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - Java 17 as Programming language
* [OpenAPI](https://github.com/OAI/OpenAPI-Specification) - for managing the API
* [PostgreSQL](https://www.postgresql.org/) - Database for maintaining tournament, match and customer details
* [Docker](https://www.docker.com/) - for running app in cloud

### 2.2 Key Features to highlight:

1. This app currently accepts tournament type as TENNIS, although this can be extended in future for other type of tournaments.
2. Uses OpenAPI specification for the defined APIs along with all mandatory attribute checks
3. Comprehensive Unit tests and Integration tests(including H2 database) which covers all the endpoints
4. GET /customer/{customerId}/licensed/matches is provided a response payload which supports pagination, Currently this reports all the entries in page 0 but this can be enhanced to paginate the response in case the number of licensed matches for a customer is increasing
5. All the POST, PATCH requests which creates or updates the resource accepts any valid offsetdatetime stamp but while storing in DB its stored in UTC
6. All the GET endpoints which contains the match startDateTime is retrieved as UTC from the DB but displayed in system default timezone format. Integration test has a check for validating a different timezone


### 2.3 Solution & Assumptions

1. This app is built with spring reactive framework, which enables non-blocking asynchronous programming which is essential for low-latency, high-throughput applications
2. PATCH /customer/{customerId}/matches - updates the customer with list of matches provided in the request. This request generates a new licenseId for the given set of matches in the request 
3. GET /customer/{customerId}/licensed/matches - retrieves the customer licensed matches. License is an entity is assumed to be created everytime when there is a PATCH request to update the number of matches subscribed by given customer
4. All the resource creation and update endpoints are protected by transaction management. In case of any issues in the endpoint, transactions will be rolled back automatically
5. This APP is currently not secured by spring security, all the endpoints can be accessed without any auth. This is left for future enhancements to give more priority to the changes

<hr>

## 3.OpenAPI Spec

If this application is being accessed locally,then OpenAPI spec can be accessed at

/src/main/resources/specs/openapi.yml

The open API spec content can be pasted and viewed at any swagger viewers like https://editor.swagger.io/ 

<hr>

## 4.Run Application

This application is preconfigured with required properties and doesn't require any external properties to start

#### 

Below command can be used to invoke the application

mvn spring-boot:run


This application has a postman collection as well to interact with all the endpoints. 
This postman collection is populated with variables which are automatically managed and all the endpoints from top to bottom can be triggered in sequential order which sets the environment variable required for subsequent calls. The collection also has working example responses saved

<hr>

## 5. Future Enhancements

1. Pagination for *GET /customer/{customerId}/licensed/matches* endpoint
2. Add spring security for the project
3. Add docker for the app


