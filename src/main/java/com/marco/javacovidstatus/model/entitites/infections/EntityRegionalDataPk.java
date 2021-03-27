package com.marco.javacovidstatus.model.entitites.infections;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * This is the primary key of the region data
 * 
 * @author Marco
 *
 */
@Embeddable
public class EntityRegionalDataPk implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "DATE_DATA")
    private LocalDate date;
    @Column(name = "REGION_CODE")
    private String regionCode;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((regionCode == null) ? 0 : regionCode.hashCode());
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
        EntityRegionalDataPk other = (EntityRegionalDataPk) obj;
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
        return true;
    }

}
