package com.marco.javacovidstatus.model.dto;

import com.marco.javacovidstatus.enums.Gender;

public class PopulationDto {
    private int year;
    private int age;
    private Gender gender;
    private int counter;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
