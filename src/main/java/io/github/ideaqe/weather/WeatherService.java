package io.github.ideaqe.weather;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

@Path("/weather")
public class WeatherService {

	Response responseTemperatures;


	@Created
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public void createMeasurement(Observation observation) {
		Observations.getInstance().add(observation);
	}

	@GET
	@Path("/{stationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Observation> getObservations(@PathParam("stationId") int stationId) {
		return Observations.getInstance().getObservations(stationId);
	}

	@GET
	@Path("/{stationId}/{observationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Observation getObservation(@PathParam("stationId") int stationId,
			@PathParam("observationId") int observationId) {
		return Observations.getInstance().getObservation(stationId, observationId);
	}

	@GET
	@Path("/temperatureRange/{stationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemperatures(@PathParam("stationId") int stationId) {
		responseTemperatures = Observations.getInstance().getObservationTemparatures(stationId);
		return responseTemperatures;
	}
	
	@GET
	@Path("/temperatureDateRange/{stationId}/startDate/{startDate}/endDate/{endDate}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTemperaturesDateRange(@PathParam("stationId") int stationId,
			@PathParam("startDate") String startDate,
			@PathParam("endDate") String endDate) {
		responseTemperatures = Observations.getInstance().getObservationTemparaturesDateRange(stationId, startDate, endDate);
		return responseTemperatures;
	}
	
	@ThreadSafe
	private static final class Observations {

		private final Map<Integer, Map<Integer, Observation>> observations = new ConcurrentHashMap();
		private static Logger logger = LoggerFactory.getLogger(Observations.class);
		private static final Observations INSTANCE = new Observations();


		private Observations() {
			initialize();
		}

		public static Observations getInstance() {
			return INSTANCE;
		}

		private void initialize() {
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			ObjectReader reader = mapper.readerFor(Observation.class).with(schema);
			try {
				MappingIterator<Observation> csvData =
						reader.readValues(Observations.class.getResourceAsStream("/data.csv"));

				csvData.readAll()
				.stream()
				.forEach(observation ->
				observations.computeIfAbsent(observation.stationId, key -> new ConcurrentHashMap<>())
				.put(observation.observationId, observation));

			} catch (IOException ex) {
				logger.warn("Could not initialize with prepared CSV file.", ex);
			}
		}

		public Collection<Observation> getObservations(int stationId) {
			ensureExistence(stationId);
			return observations.get(stationId).values();
		}

		public Response getObservationTemparatures(int stationId) {
			ensureExistence(stationId);
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			ObjectReader reader = mapper.reader(Observation.class).with(schema);
			try {

				String fileName="temperatureRange.json";
				StringWriter stringResp = new StringWriter();
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);

				String responseJsonData = getStringFromInputStream(in);

				try {
					in.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

				try {
					Temperature temperatureBean = objectMapper.readValue(responseJsonData,Temperature.class);
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				MappingIterator<Observation> csvData;
				try {
					csvData = reader.readValues(Observation.class.getResourceAsStream("/data.csv"));
					List<Observation> observations = new ArrayList<>();
					List<Float> temperatures = new ArrayList<>();

					while (csvData.hasNext()){
						Observation row = csvData.next();

						if (row.stationId == stationId){
							temperatures.add(row.temperature);
						}
					}

					Collections.sort(temperatures);
					float minimum = (temperatures.get(0));
					float maximum =  (temperatures.get(temperatures.size()-1));					
					float average = 0;
					for (int i =0; i<temperatures.size(); i++){						
						average = average + temperatures.get(i);
					}					
					average = average/temperatures.size();
					Temperature temp = new Temperature(minimum, maximum, average);
					temp.setAverage();
					temp.setMaximum();
					temp.setMinimum();
					objectMapper.writeValue(stringResp, temp);

				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return  Response.status(200).entity(stringResp.toString()).header("ErrorCode", "").header("ErrorString", "").build();

			}
			finally{

			}

		}
		
		public Response getObservationTemparaturesDateRange(int stationId, String startDate, String endDate) {
			ensureExistence(stationId);
			CsvSchema schema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			ObjectReader reader = mapper.reader(Observation.class).with(schema);
			try {

				String fileName="temperatureRange.json";
				StringWriter stringResp = new StringWriter();
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);

				String responseJsonData = getStringFromInputStream(in);

				try {
					in.close();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

				try {
					Temperature temperatureBean = objectMapper.readValue(responseJsonData,Temperature.class);
				} catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				MappingIterator<Observation> csvData;
				try {
					csvData = reader.readValues(Observation.class.getResourceAsStream("/data.csv"));
					List<Observation> observations = new ArrayList<>();
					List<Float> temperatures = new ArrayList<>();
					
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
					
					 while (csvData.hasNext()){
						Observation row = csvData.next();
	
					   try {
						   
						   Date endDateRange = dateFormatter.parse(endDate);
						   Date startDateRange = dateFormatter.parse(startDate);
							if (row.stationId == stationId) {												
								if ((row.timestamp.compareTo(endDateRange))<=0 && (row.timestamp.compareTo(startDateRange))>0){
									temperatures.add(row.temperature);								
							
							    }
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					Collections.sort(temperatures);
					float minimum = (temperatures.get(0));
					float maximum =  (temperatures.get(temperatures.size()-1));			
					float average = 0;
					for (int i =0; i<temperatures.size(); i++){
						
						average = average + temperatures.get(i);
					}		
					average = average/temperatures.size();
					Temperature temp = new Temperature(minimum, maximum, average);
					temp.setAverage();
					temp.setMaximum();
					temp.setMinimum();
					objectMapper.writeValue(stringResp, temp);

				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return  Response.status(200).entity(stringResp.toString()).header("ErrorCode", "").header("ErrorString", "").build();

			}
			finally{

			}

		}


		public void add(Observation observation) {
			Observation nullIfAssociated = observations
					.computeIfAbsent(observation.stationId, key -> new ConcurrentHashMap<>())
					.putIfAbsent(observation.observationId, observation);

			if (nullIfAssociated != null) {
				throw new CollisionException(
						String.format("Observation for station %s with id %s already exists.",
								observation.stationId, observation.observationId));
			}
		}

		public Observation getObservation(int stationId, int observationId) {
			ensureExistence(stationId, observationId);
			return observations.get(stationId).get(observationId);
		}

		private void ensureExistence(int stationId, int observationId) {
			ensureExistence(stationId);
			if (!observations.get(stationId).containsKey(observationId)) {
				throw new NotFoundException();
			}
		}

		private void ensureExistence(int stationId) {
			if (!observations.containsKey(stationId)) {
				throw new NotFoundException();
			}
		}
	}

	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
