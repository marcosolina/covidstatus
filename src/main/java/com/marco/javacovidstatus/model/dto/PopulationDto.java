package com.marco.javacovidstatus.model.dto;

import com.marco.javacovidstatus.enums.Gender;

/**
 * This model is used to pass the information related to the Italian population
 * 
 * @author Marco
 *
 */
public class PopulationDto {
	private int year;
	private String regionCode;
	private int age;
	private Gender gender;
	private int counter;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

}
