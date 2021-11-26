package com.marco.javacovidstatus.model.entitites.vaccines;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This entity represents the data of the vaccines given
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "SOMMINISTRAZIONI_VACCINI")
public class EntitySomministrazioneVaccini {

    @EmbeddedId
    private EntitySomministrazioneVacciniPk id;
    @Column(name = "MEN_COUNTER")
    private int menCounter;
    @Column(name = "WOMEN_COUNTER")
    private int womenCounter;
    @Column(name = "NHS_PEOPLE_COUNTER")
    private int nhsPeopleCounter;
    @Column(name = "NON_NHS_PEOPLE_COUNTER")
    private int nonNhsPeopleCounter;
    @Column(name = "NURSING_HOME_COUNTER")
    private int nursingHomeCounter;
    @Column(name = "AGE_60_69_COUNTER")
    private int age6069counter;
    @Column(name = "AGE_70_79_COUNTER")
    private int age7079counter;
    @Column(name = "OVER_80_COUNTER")
    private int over80Counter;
    @Column(name = "PUBLIC_ORDER_COUNTER")
    private int publicOrderCounter;
    @Column(name = "SCHOOL_STAFF_COUNTER")
    private int schoolStaffCounter;
    @Column(name = "FRAGILE_PEOPLE_COUNTER")
    private int fragilePeopleCounter;
    @Column(name = "OTHER_PEOPLE_COUNTER")
    private int otherPeopleCounter;
    @Column(name = "FIRST_DOSE_COUNTER")
    private int firstDoseCounter;
    @Column(name = "SECOND_DOSE_COUNTER")
    private int secondDoseCounter;
    @Column(name = "MONO_DOSE_COUNTER")
    private int monoDoseCounter;
    @Column(name = "DOSE_AFTER_INFECT_COUNTER")
    private int doseAfterInfectCounter;
    @Column(name = "THIRD_DOSE_COUNTER")
    private int thirdDoseCounter;

    public EntitySomministrazioneVacciniPk getId() {
        return id;
    }

    public void setId(EntitySomministrazioneVacciniPk id) {
        this.id = id;
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
}
