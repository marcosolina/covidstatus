package com.marco.javacovidstatus.model.dto;

/**
 * Enum used to indicate the data types
 * 
 * @author Marco
 *
 */
public enum CovidDataType {
    NEW_INFECTIONS("Nuove Infezioni"), 
    NEW_TESTS("Test Eseguiti"), 
    PERC_INFECTIONS("% Infetti su Tamponi eseguiti"),
    CASUALTIES("Decessi"), 
    PERC_CASUALTIES("% Decessi su infetti"), 
    NEW_HOSPITALISED("Nuovi Ricoveri"),
    NEW_INTENSIVE_THERAPY("Ricoveri Terapia intensiva (Di Cui)"), 
    NEW_RECOVER("Nuovi Dismess/Guariti"),
    ;

    private String desc;

    private CovidDataType(String desc) {
        this.desc = desc;
    }

    public String getDescription() {
        return this.desc;
    }
}
