# weather-java - Craft Demo
[![Build Status](https://travis-ci.org/balajiarunachalam/weather.svg?branch=develop)](https://travis-ci.org/balajiarunchalam/weather)

Regarding API see the [weather](https://github.com/balajiarunachalam/weather-java) repository.

## Build

Build and run the project via Gradle:
```bash
# build project
$ gradle clean build

# task for creating fat Jar file
$ gradle createJar

# executing fat jar (runs service on port 8080)
$ java -jar build/libs/weather-all-0.1.0-SNAPSHOT.jar
```


# QE Weather Sample

## API

| Method | Endpoint                                                   | Status      | Media Type       |
|--------|------------------------------------------------------------|-------------|------------------|
| POST   | http://{server}:{port}/weather/                            | 201 Created | application/json |
| GET    | http://{server}:{port}/weather/{stationId}/{observationId} | 200 OK      | application/json |
| GET    | http://{server}:{port}/weather/{stationId}                 | 200 OK      | application/json |

### Sample POST Payload
```json
{
  "temperature": 60,
  "humidity": 37.78,
  "precipitation": 5.27,
  "observationId": 12345,
  "stationId": 1,
  "timestamp": "2016-11-20T04:00:00.000+0000"
}
```

### Sample GET Responses

#### /weather/{stationId}
```json
[
  {
    "temperature": 60,
    "humidity": 37.78,
    "precipitation": 5.27,
    "observationId": 1,
    "stationId": 1,
    "timestamp": "2016-11-20T04:00:00.000+0000"
  },
  {
    "temperature": 60,
    "humidity": 37.93,
    "precipitation": 4,
    "observationId": 8,
    "stationId": 1,
    "timestamp": "2016-11-20T04:35:00.000+0000"
  },
  {
    "temperature": 60,
    "humidity": 37.78,
    "precipitation": 5.27,
    "observationId": 12345,
    "stationId": 1,
    "timestamp": "2016-11-20T04:00:00.000+0000"
  }
]
```

#### /weather/{stationId}/{observationId}
```json
{
  "temperature": 60,
  "humidity": 37.93,
  "precipitation": 4,
  "observationId": 8,
  "stationId": 1,
  "timestamp": "2016-11-20T04:35:00.000+0000"
}
```


