package com.marco.javacovidstatus.model.entitites.vaccines;

/**
 * Total number of people vaccinated per region.
 * 
 * @author Marco
 *
 */
public class VaccinesGivenPerRegion {
    private String regionCode;
    private Long firstDose;
    private Long secondDose;
    private Long monoDose;
    private Long doseAfterInfection;
    private Long thirdDoseCounter;
    private Long fourthDoseCounter;
    private Long fifthDoseCounter;

    public VaccinesGivenPerRegion(String regionCode, Long firstDose, Long secondDose, Long monoDose, Long doseAfterInfection, Long thirdDoseCounter, Long fourthDoseCounter, Long fifthDoseCounter) {
        super();
        this.regionCode = regionCode;
        this.firstDose = firstDose;
        this.secondDose = secondDose;
        this.monoDose = monoDose;
        this.doseAfterInfection = doseAfterInfection;
        this.thirdDoseCounter = thirdDoseCounter;
        this.fourthDoseCounter = fourthDoseCounter;
        this.fifthDoseCounter = fifthDoseCounter;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
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
