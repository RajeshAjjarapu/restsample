package io.github.ideaqe.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Observation {

    public final int observationId, stationId;
    public final float temperature, humidity, precipitation;
    public final Date timestamp;

    public Observation(@JsonProperty("observation_id") int observationId, @JsonProperty("station_id") int stationId,
            @JsonProperty("temperature") float temperature, @JsonProperty("humidity") float humidity,
            @JsonProperty("precipitation") float precipitation, @JsonProperty("observation_time") Date timestamp) {
        this.observationId = observationId;
        this.stationId = stationId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipitation = precipitation;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Observation{" +
                "observationId=" + observationId +
                ", stationId=" + stationId +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", precipitation=" + precipitation +
                ", timestamp=" + timestamp +
                '}';
    }
}
