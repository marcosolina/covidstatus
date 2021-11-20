package com.marco.javacovidstatus.model.entitites.italianpopulation;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EntityBoosterPopulationPk implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "REGION_CODE")
    private String regionCode;
    @Column(name = "AGE")
    private int age;

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

    @Override
    public int hashCode() {
        return Objects.hash(age, regionCode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityBoosterPopulationPk other = (EntityBoosterPopulationPk) obj;
        return age == other.age && Objects.equals(regionCode, other.regionCode);
    }

}
