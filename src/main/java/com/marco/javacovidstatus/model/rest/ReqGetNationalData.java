package com.marco.javacovidstatus.model.rest;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This represents the HTTP request to retrieve the data at National level
 * 
 * @author Marco
 *
 */
public class ReqGetNationalData implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate from;
    private LocalDate to;

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

}
