package com.marco.javacovidstatus.model.rest.infections;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This represents the HTTP request to retrieve the data at Province level
 * 
 * @author Marco
 *
 */
public class ReqGetProvinceData implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate from;
    private LocalDate to;
    private String regionCode;

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
