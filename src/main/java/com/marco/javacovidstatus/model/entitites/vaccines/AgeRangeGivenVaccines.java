package com.marco.javacovidstatus.model.entitites.vaccines;

/**
 * Custom Query Model used to retrieve number of given vaccines grouped by age
 * 
 * @author Marco
 *
 */
public class AgeRangeGivenVaccines {
    private String ageRange;
    private Long firstDose;
    private Long secondDose;
    private Long monoDose;
    private Long doseAfterInfection;
    private Long thirdDoseCounter;
    private Long fourthDoseCounter;
    private Long fifthDoseCounter;

    public AgeRangeGivenVaccines(String ageRange, Long firstDose, Long secondDose, Long monoDose, Long doseAfterInfection, Long thirdDoseCounter, Long fourthDoseCounter, Long fifthDoseCounter) {
        super();
        this.ageRange = ageRange;
        this.firstDose = firstDose;
        this.secondDose = secondDose;
        this.monoDose = monoDose;
        this.doseAfterInfection = doseAfterInfection;
        this.thirdDoseCounter = thirdDoseCounter;
        this.fourthDoseCounter = fourthDoseCounter;
        this.fifthDoseCounter = fifthDoseCounter;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
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

    public Long getDoseAfterInfection() {
        return doseAfterInfection;
    }

    public void setDoseAfterInfection(Long doseAfterInfection) {
        this.doseAfterInfection = doseAfterInfection;
    }

    public Long getThirdDoseCounter() {
        return thirdDoseCounter;
    }

    public void setThirdDoseCounter(Long thirdDoseCounter) {
        this.thirdDoseCounter = thirdDoseCounter;
    }

    public Long getFourthDoseCounter() {
        return fourthDoseCounter;
    }

    public void setFourthDoseCounter(Long fourthDoseCounter) {
        this.fourthDoseCounter = fourthDoseCounter;
    }

	public Long getFifthDoseCounter() {
		return fifthDoseCounter;
	}

	public void setFifthDoseCounter(Long fifthDoseCounter) {
		this.fifthDoseCounter = fifthDoseCounter;
	}
}
