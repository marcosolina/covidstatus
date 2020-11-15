package com.marco.javacovidstatus.model.entitites;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This entity is used to store the data retrieved from the national institution
 * 
 * @author Marco
 *
 */
@Entity
@Table(name = "PROVINCE_DATA")
public class EntityProvinceData {

    @EmbeddedId
    private EntityProvinceDataPk id;
    @Column(name = "REGION_DESC")
    private String regionDesc;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "PROVINCE_SHORT")
    private String shortName;
    @Column(name = "NEW_INFECTIONS")
    private int newInfections;

    public EntityProvinceDataPk getId() {
        return id;
    }

    public void setId(EntityProvinceDataPk id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getNewInfections() {
        return newInfections;
    }

    public void setNewInfections(int newInfections) {
        this.newInfections = newInfections;
    }

    public String getRegionDesc() {
        return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
        this.regionDesc = regionDesc;
    }

}
