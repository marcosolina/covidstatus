package com.marco.javacovidstatus.model.entitites.vaccines;

/**
 * Custom Query Model used to retrieve the number of given vaccines doses
 * 
 * @author Marco
 *
 */
public class DoseCounter {
    private Long firstDoseCounter;
    private Long secondDoseCounter;
    private Long monoDoseCounter;
    private Long doseAfterInfectCounter;
    private Long thirdDoseCounter;
    private Long fourthDoseCounter;
    private Long fifthDoseCounter;

    public DoseCounter(Long firstDoseCounter, Long secondDoseCounter, Long monoDoseCounter, Long doseAfterInfectCounter, Long thirdDoseCounter, Long fourthDoseCounter, Long fifthDoseCounter) {
        super();
        this.firstDoseCounter = firstDoseCounter;
        this.secondDoseCounter = secondDoseCounter;
        this.monoDoseCounter = monoDoseCounter;
        this.doseAfterInfectCounter = doseAfterInfectCounter;
        this.thirdDoseCounter = thirdDoseCounter;
        this.fourthDoseCounter = fourthDoseCounter;
        this.fifthDoseCounter = fifthDoseCounter;
    }

    public Long getFirstDoseCounter() {
        return firstDoseCounter;
    }

    public void setFirstDoseCounter(Long firstDoseCounter) {
        this.firstDoseCounter = firstDoseCounter;
    }

    public Long getSecondDoseCounter() {
        return secondDoseCounter;
    }

    public void setSecondDoseCounter(Long secondDoseCounter) {
        this.secondDoseCounter = secondDoseCounter;
    }

    public Long getMonoDoseCounter() {
        return monoDoseCounter;
    }

    public void setMonoDoseCounter(Long monoDoseCounter) {
        this.monoDoseCounter = monoDoseCounter;
    }

    public Long getDoseAfterInfectCounter() {
        return doseAfterInfectCounter;
    }

    public void setDoseAfterInfectCounter(Long doseAfterInfectCounter) {
        this.doseAfterInfectCounter = doseAfterInfectCounter;
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
