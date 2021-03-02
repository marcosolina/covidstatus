package com.marco.javacovidstatus.model.entitites;

public class VeccinesDeliveredPerSupplier {

	private String supplier;
	private Long dosesDelivered;

	public VeccinesDeliveredPerSupplier(String supplier, Long dosesDelivered) {
		super();
		this.supplier = supplier;
		this.dosesDelivered = dosesDelivered;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public Long getDosesDelivered() {
		return dosesDelivered;
	}

	public void setDosesDelivered(Long dosesDelivered) {
		this.dosesDelivered = dosesDelivered;
	}

}
