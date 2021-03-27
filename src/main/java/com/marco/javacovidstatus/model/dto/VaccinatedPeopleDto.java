package com.marco.javacovidstatus.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class Represents the data for the given vaccines info per day
 * 
 * @author Marco
 *
 */
public class VaccinatedPeopleDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String regionCode;
	private LocalDate date;
	private String supplier;
	private String ageRange;
	private int menCounter;
	private int womenCounter;
	private int nhsPeopleCounter;
	private int nonNhsPeopleCounter;
	private int nursingHomeCounter;
	private int over80Counter;
	private int publicOrderCounter;
	private int schoolStaffCounter;
	private int otherPeopleCounter;
	private int firstDoseCounter;
	private int secondDoseCounter;

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public int getMenCounter() {
		return menCounter;
	}

	public void setMenCounter(int menCounter) {
		this.menCounter = menCounter;
	}

	public int getWomenCounter() {
		return womenCounter;
	}

	public void setWomenCounter(int womenCounter) {
		this.womenCounter = womenCounter;
	}

	public int getNhsPeopleCounter() {
		return nhsPeopleCounter;
	}

	public void setNhsPeopleCounter(int nhsPeopleCounter) {
		this.nhsPeopleCounter = nhsPeopleCounter;
	}

	public int getNonNhsPeopleCounter() {
		return nonNhsPeopleCounter;
	}

	public void setNonNhsPeopleCounter(int nonNhsPeopleCounter) {
		this.nonNhsPeopleCounter = nonNhsPeopleCounter;
	}

	public int getNursingHomeCounter() {
		return nursingHomeCounter;
	}

	public void setNursingHomeCounter(int nursingHomeCounter) {
		this.nursingHomeCounter = nursingHomeCounter;
	}

	public int getOver80Counter() {
		return over80Counter;
	}

	public void setOver80Counter(int over80Counter) {
		this.over80Counter = over80Counter;
	}

	public int getPublicOrderCounter() {
		return publicOrderCounter;
	}

	public void setPublicOrderCounter(int publicOrderCounter) {
		this.publicOrderCounter = publicOrderCounter;
	}

	public int getSchoolStaffCounter() {
		return schoolStaffCounter;
	}

	public void setSchoolStaffCounter(int schoolStaffCounter) {
		this.schoolStaffCounter = schoolStaffCounter;
	}

	public int getFirstDoseCounter() {
		return firstDoseCounter;
	}

	public void setFirstDoseCounter(int firstDoseCounter) {
		this.firstDoseCounter = firstDoseCounter;
	}

	public int getSecondDoseCounter() {
		return secondDoseCounter;
	}

	public void setSecondDoseCounter(int secondDoseCounter) {
		this.secondDoseCounter = secondDoseCounter;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getOtherPeopleCounter() {
		return otherPeopleCounter;
	}

	public void setOtherPeopleCounter(int otherPeopleCounter) {
		this.otherPeopleCounter = otherPeopleCounter;
	}

}
