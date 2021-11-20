package com.marco.javacovidstatus.model.rest;

import com.marco.utils.http.MarcoResponse;

public class GetRefreshStatusResp extends MarcoResponse {
    private boolean isRefreshingData;
    private String lastUpdate;

    public boolean isRefreshingData() {
        return isRefreshingData;
    }

    public void setRefreshingData(boolean isRefreshingData) {
        this.isRefreshingData = isRefreshingData;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
