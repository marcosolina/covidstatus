package com.marco.javacovidstatus.repositories.sql;

import java.util.List;

import com.marco.javacovidstatus.model.Region;

public interface CovidRepository {

    public List<Region> getRegionList();
}
