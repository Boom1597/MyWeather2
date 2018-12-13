package com.example.myweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hp on 2018/12/11.
 */

public class Settings extends DataSupport {

    private String updateDate;//更新时间
    private String TempUnits;//气温单位
    private boolean isNotify;//是否通知

    public Settings(){}

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getTempUnits() {
        return TempUnits;
    }

    public void setTempUnits(String tempUnits) {
        TempUnits = tempUnits;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

}
