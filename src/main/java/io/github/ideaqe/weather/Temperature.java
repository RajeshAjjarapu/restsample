package io.github.ideaqe.weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Temperature {
	
	
    public float minimum;
	public float average;
	public float maximum;

@JsonCreator
public Temperature(@JsonProperty("minimum") float minimum, 
		@JsonProperty("maximum") float maximum, 
		@JsonProperty ("average") float average){
		
		this.minimum = minimum;
		this.maximum =  maximum;
		this.average = average;
	}


    
    public Float getMinimum(){
    	return minimum;
    }
    
    public Float getMaximum(){
    	return maximum;
    }
    
    public Float getAverage(){
    	return average;
    }
    
    public void setMinimum(){
    	this.minimum=minimum;
    }
    
    public void setMaximum(){
    	this.maximum=maximum;
    }
    
    public void setAverage(){
    	this.average=average;
    }


  
}
