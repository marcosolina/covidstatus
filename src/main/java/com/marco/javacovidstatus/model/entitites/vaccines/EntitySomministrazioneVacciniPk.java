package com.marco.javacovidstatus.model.entitites.vaccines;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * This class represent the primary of the given vaccines table
 * 
 * @author Marco
 *
 */
@Embeddable
public class EntitySomministrazioneVacciniPk implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "REGION_CODE")
	private String regionCode;
	@Column(name = "DATE_DATA")
	private LocalDate date;
	@Column(name = "SUPPLIER")
	private String supplier;
	@Column(name = "AGE_RANGE")
	private String ageRange;

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ageRange == null) ? 0 : ageRange.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((regionCode == null) ? 0 : regionCode.hashCode());
		result = prime * result + ((supplier == null) ? 0 : supplier.hashCode());
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
		EntitySomministrazioneVacciniPk other = (EntitySomministrazioneVacciniPk) obj;
		if (ageRange == null) {
			if (other.ageRange != null)
				return false;
		} else if (!ageRange.equals(other.ageRange))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (regionCode == null) {
			if (other.regionCode != null)
				return false;
		} else if (!regionCode.equals(other.regionCode))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;
		return true;
	}

}
