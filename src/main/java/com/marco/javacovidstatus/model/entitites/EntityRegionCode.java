package com.marco.javacovidstatus.model.entitites;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class represents the DB record for the region code
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "CODICI_REGIONI")
public class EntityRegionCode {
	@Id
	@Column(name = "AREA")
	private String area;
	@Column(name = "REGION_CODE")
	private String regionCode;
	@Column(name = "DESCR")
	private String desc;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
