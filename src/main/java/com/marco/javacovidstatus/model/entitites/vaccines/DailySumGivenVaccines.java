package com.marco.javacovidstatus.model.entitites.vaccines;

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
    private long age6069counter;
    private long age7079counter;
    private long over80Counter;
    private long publicOrderCounter;
    private long schoolStaffCounter;
    private long fragilePeopleCounter;
    private long otherPeopleCounter;
    private long firstDoseCounter;
    private long secondDoseCounter;
    private long monoDoseCounter;
    private long doseAfterInfectCounter;

    public DailySumGivenVaccines(LocalDate date, long menCounter, long womenCounter, long nhsPeopleCounter,
            long nonNhsPeopleCounter, long nursingHomeCounter, long age6069counter, long age7079counter,
            long over80Counter, long publicOrderCounter, long schoolStaffCounter, long fragilePeopleCounter,
            long otherPeopleCounter, long firstDoseCounter, long secondDoseCounter, long monoDoseCounter,
            long doseAfterInfectCounter) {
        super();
        this.date = date;
        this.menCounter = menCounter;
        this.womenCounter = womenCounter;
        this.nhsPeopleCounter = nhsPeopleCounter;
        this.nonNhsPeopleCounter = nonNhsPeopleCounter;
        this.nursingHomeCounter = nursingHomeCounter;
        this.age6069counter = age6069counter;
        this.age7079counter = age7079counter;
        this.over80Counter = over80Counter;
        this.publicOrderCounter = publicOrderCounter;
        this.schoolStaffCounter = schoolStaffCounter;
        this.fragilePeopleCounter = fragilePeopleCounter;
        this.otherPeopleCounter = otherPeopleCounter;
        this.firstDoseCounter = firstDoseCounter;
        this.secondDoseCounter = secondDoseCounter;
        this.monoDoseCounter = monoDoseCounter;
        this.doseAfterInfectCounter = doseAfterInfectCounter;
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

    public long getAge6069counter() {
        return age6069counter;
    }

    public void setAge6069counter(long age6069counter) {
        this.age6069counter = age6069counter;
    }

    public long getAge7079counter() {
        return age7079counter;
    }

    public void setAge7079counter(long age7079counter) {
        this.age7079counter = age7079counter;
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

    public long getFragilePeopleCounter() {
        return fragilePeopleCounter;
    }

    public void setFragilePeopleCounter(long fragilePeopleCounter) {
        this.fragilePeopleCounter = fragilePeopleCounter;
    }

    public long getOtherPeopleCounter() {
        return otherPeopleCounter;
    }

    public void setOtherPeopleCounter(long otherPeopleCounter) {
        this.otherPeopleCounter = otherPeopleCounter;
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

    public long getMonoDoseCounter() {
        return monoDoseCounter;
    }

    public void setMonoDoseCounter(long monoDoseCounter) {
        this.monoDoseCounter = monoDoseCounter;
    }

    public long getDoseAfterInfectCounter() {
        return doseAfterInfectCounter;
    }

    public void setDoseAfterInfectCounter(long doseAfterInfectCounter) {
        this.doseAfterInfectCounter = doseAfterInfectCounter;
    }

}
