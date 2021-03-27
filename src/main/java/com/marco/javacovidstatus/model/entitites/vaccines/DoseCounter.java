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

	public DoseCounter(Long firstDoseCounter, Long secondDoseCounter) {
		super();
		this.firstDoseCounter = firstDoseCounter;
		this.secondDoseCounter = secondDoseCounter;
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

}
