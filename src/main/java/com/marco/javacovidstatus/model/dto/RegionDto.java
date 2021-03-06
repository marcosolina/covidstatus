package com.marco.javacovidstatus.model.dto;

/**
 * This class represents the Region definition
 * 
 * @author Marco
 *
 */
public class RegionDto {

    private String code;
    private String desc;

    public RegionDto() {

    }

    public RegionDto(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
