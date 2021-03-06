package com.marco.javacovidstatus.model.entitites;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Custom Query Model used to retrieve number of given vaccines per "people
 * type"
 * 
 * @author Marco
 *
 */
public class DailySumGivenVaccines implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate date;
	private long menCounter;
	private long womenCounter;
	private long nhsPeopleCounter;
	private long nonNhsPeopleCounter;
	private long nursingHomeCounter;
	private long over80Counter;
	private long publicOrderCounter;
	private long schoolStaffCounter;
	private long firstDoseCounter;
	private long secondDoseCounter;

	public DailySumGivenVaccines(LocalDate date, long menCounter, long womenCounter, long nhsPeopleCounter,
			long nonNhsPeopleCounter, long nursingHomeCounter, long over80Counter, long publicOrderCounter,
			long schoolStaffCounter, long firstDoseCounter, long secondDoseCounter) {
		super();
		this.date = date;
		this.menCounter = menCounter;
		this.womenCounter = womenCounter;
		this.nhsPeopleCounter = nhsPeopleCounter;
		this.nonNhsPeopleCounter = nonNhsPeopleCounter;
		this.nursingHomeCounter = nursingHomeCounter;
		this.over80Counter = over80Counter;
		this.publicOrderCounter = publicOrderCounter;
		this.schoolStaffCounter = schoolStaffCounter;
		this.firstDoseCounter = firstDoseCounter;
		this.secondDoseCounter = secondDoseCounter;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public long getMenCounter() {
		return menCounter;
	}

	public void setMenCounter(long menCounter) {
		this.menCounter = menCounter;
	}

	public long getWomenCounter() {
		return womenCounter;
	}

	public void setWomenCounter(long womenCounter) {
		this.womenCounter = womenCounter;
	}

	public long getNhsPeopleCounter() {
		return nhsPeopleCounter;
	}

	public void setNhsPeopleCounter(long nhsPeopleCounter) {
		this.nhsPeopleCounter = nhsPeopleCounter;
	}

	public long getNonNhsPeopleCounter() {
		return nonNhsPeopleCounter;
	}

	public void setNonNhsPeopleCounter(long nonNhsPeopleCounter) {
		this.nonNhsPeopleCounter = nonNhsPeopleCounter;
	}

	public long getNursingHomeCounter() {
		return nursingHomeCounter;
	}

	public void setNursingHomeCounter(long nursingHomeCounter) {
		this.nursingHomeCounter = nursingHomeCounter;
	}

	public long getOver80Counter() {
		return over80Counter;
	}

	public void setOver80Counter(long over80Counter) {
		this.over80Counter = over80Counter;
	}

	public long getPublicOrderCounter() {
		return publicOrderCounter;
	}

	public void setPublicOrderCounter(long publicOrderCounter) {
		this.publicOrderCounter = publicOrderCounter;
	}

	public long getSchoolStaffCounter() {
		return schoolStaffCounter;
	}

	public void setSchoolStaffCounter(long schoolStaffCounter) {
		this.schoolStaffCounter = schoolStaffCounter;
	}

	public long getFirstDoseCounter() {
		return firstDoseCounter;
	}

	public void setFirstDoseCounter(long firstDoseCounter) {
		this.firstDoseCounter = firstDoseCounter;
	}

	public long getSecondDoseCounter() {
		return secondDoseCounter;
	}

	public void setSecondDoseCounter(long secondDoseCounter) {
		this.secondDoseCounter = secondDoseCounter;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
