package com.marco.javacovidstatus.model.entitites;

import com.marco.javacovidstatus.model.entitites.infections.EntityProvinceData;

/**
 * This class represents a subset info of the {@link EntityProvinceData}}
 * 
 * @author Marco
 *
 */
public class RegionData {

    private String regionCode;
    private String regionDesc;

    public RegionData(String regionCode, String regionDesc) {
        super();
        this.regionCode = regionCode;
        this.regionDesc = regionDesc;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionDesc() {
        return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
        this.regionDesc = regionDesc;
    }

}
