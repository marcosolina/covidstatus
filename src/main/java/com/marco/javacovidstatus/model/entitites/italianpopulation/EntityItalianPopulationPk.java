package com.marco.javacovidstatus.model.entitites.italianpopulation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.marco.javacovidstatus.enums.Gender;

/**
 * Primary key for {@link EntityItalianPopulation}
 * @author Marco
 *
 */
@Embeddable
public class EntityItalianPopulationPk implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "YEAR")
    private int year;
    @Column(name = "AGE")
    private int age;
    @Column(name = "GENDER")
    @Enumerated(EnumType.STRING)
    private Gender gender;

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

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityItalianPopulationPk other = (EntityItalianPopulationPk) obj;
        if (age != other.age)
            return false;
        if (gender != other.gender)
            return false;
        if (year != other.year)
            return false;
        return true;
    }

}
