package com.marco.javacovidstatus.model.entitites;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This entity represents the data of the vaccines delivered
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "VACCINI_CONSEGNE")
public class EntityVacciniConsegne {

	@EmbeddedId
	private EntityVacciniConsegnePk id;
	@Column(name = "DOSES_DELIVERED")
	private int dosesDelivered;

	public EntityVacciniConsegnePk getId() {
		return id;
	}

	public void setId(EntityVacciniConsegnePk id) {
		this.id = id;
	}

	public int getDosesDelivered() {
		return dosesDelivered;
	}

	public void setDosesDelivered(int dosesDelivered) {
		this.dosesDelivered = dosesDelivered;
	}

	@Override
	public String toString() {
		return "EntityVacciniConsegne [id=" + id + ", dosesDelivered=" + dosesDelivered + "]";
	}
}
