package io.github.ideaqe.tests;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.ideaqe.util.RestClient;

public class WeatherRangeTests {
	
	@Test(dataProvider = "temparatureTests" )
	public void testWeatherRange(String stationId, String minimum, String maximum, String average){
		String pathParam ="/temperatureRange/"+stationId;
		
		RestClient restClient = new RestClient();
		HttpResponse response = restClient.httpResponse(pathParam);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
		try {
			String jsonString = EntityUtils.toString(response.getEntity());
			ObjectMapper mapper = new ObjectMapper();
		    JsonNode actualObj = mapper.readTree(jsonString);
		   	    
		    Assert.assertEquals(actualObj.findValue("minimum").toString(), minimum);
		    Assert.assertEquals(actualObj.findValue("maximum").toString(), maximum);
		    Assert.assertEquals(actualObj.findValue("average").toString(), average);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	@Test(dataProvider = "temparatureNegativeTests" )
	public void testWeatherRangeNegative(String stationId){
		String pathParam ="/temperatureRange/"+stationId;
		
		RestClient restClient = new RestClient();
		HttpResponse response = restClient.httpResponse(pathParam);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), 404);
		
		
	}
	@DataProvider
	public Object[][] temparatureTests() {

		Object[][] result = null;
		
				
			result = new Object[][] {
				{"1", "60.0", "60.0", "60.0"},
				{"2", "68.0", "79.0", "75.0"},
				{"3", "65.0", "68.0", "66.5"},
				{"4", "55.0", "85.0", "73.0"},
				{"5", "65.0", "75.0", "70.0"},
				{"6", "55.0", "79.0", "64.666664"}
				};
		
	

		return result;

	}
	
	@DataProvider
	public Object[][] temparatureNegativeTests() {

		Object[][] result = null;
		
				
			result = new Object[][] {
				{"15"}
				
				};
		
	

		return result;

	}
	

}
