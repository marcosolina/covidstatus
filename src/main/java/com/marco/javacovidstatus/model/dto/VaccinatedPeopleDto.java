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
    private int age6069counter;
    private int age7079counter;
    private int over80Counter;
    private int publicOrderCounter;
    private int schoolStaffCounter;
    private int fragilePeopleCounter;
    private int otherPeopleCounter;
    private int firstDoseCounter;
    private int secondDoseCounter;
    private int monoDoseCounter;
    private int doseAfterInfectCounter;
    private int thirdDoseCounter;
    private int fourthDoseCounter;
    private int fifthDoseCounter;

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

    public int getAge6069counter() {
        return age6069counter;
    }

    public void setAge6069counter(int age6069counter) {
        this.age6069counter = age6069counter;
    }

    public int getAge7079counter() {
        return age7079counter;
    }

    public void setAge7079counter(int age7079counter) {
        this.age7079counter = age7079counter;
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

    public int getFragilePeopleCounter() {
        return fragilePeopleCounter;
    }

    public void setFragilePeopleCounter(int fragilePeopleCounter) {
        this.fragilePeopleCounter = fragilePeopleCounter;
    }

    public int getOtherPeopleCounter() {
        return otherPeopleCounter;
    }

    public void setOtherPeopleCounter(int otherPeopleCounter) {
        this.otherPeopleCounter = otherPeopleCounter;
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

    public int getMonoDoseCounter() {
        return monoDoseCounter;
    }

    public void setMonoDoseCounter(int monoDoseCounter) {
        this.monoDoseCounter = monoDoseCounter;
    }

    public int getDoseAfterInfectCounter() {
        return doseAfterInfectCounter;
    }

    public void setDoseAfterInfectCounter(int doseAfterInfectCounter) {
        this.doseAfterInfectCounter = doseAfterInfectCounter;
    }

    public int getThirdDoseCounter() {
        return thirdDoseCounter;
    }

    public void setThirdDoseCounter(int thirdDoseCounter) {
        this.thirdDoseCounter = thirdDoseCounter;
    }

    public int getFourthDoseCounter() {
        return fourthDoseCounter;
    }

    public void setFourthDoseCounter(int fourthDoseCounter) {
        this.fourthDoseCounter = fourthDoseCounter;
    }

    public int getFifthDoseCounter() {
		return fifthDoseCounter;
	}

	public void setFifthDoseCounter(int fifthDoseCounter) {
		this.fifthDoseCounter = fifthDoseCounter;
	}

	public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
