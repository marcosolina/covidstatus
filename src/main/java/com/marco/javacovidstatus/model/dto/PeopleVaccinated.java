package com.marco.javacovidstatus.model.dto;

/**
 * This model is use to carry the information about the vaccines given to the
 * different age rang
 * 
 * @author Marco
 *
 */
public class PeopleVaccinated {
    private String ageRange;
    private Long population;
    private Long firstDose;
    private Long secondDose;
    private Long monoDose;

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public Long getFirstDose() {
        return firstDose;
    }

    public void setFirstDose(Long firstDose) {
        this.firstDose = firstDose;
    }

    public Long getSecondDose() {
        return secondDose;
    }

    public void setSecondDose(Long secondDose) {
        this.secondDose = secondDose;
    }

    public Long getMonoDose() {
        return monoDose;
    }

    public void setMonoDose(Long monoDose) {
        this.monoDose = monoDose;
    }

}
