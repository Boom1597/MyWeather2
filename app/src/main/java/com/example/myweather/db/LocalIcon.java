package com.example.myweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hp on 2018/12/12.
 */

public class LocalIcon extends DataSupport {//本地图片
    private String iconTxt;//图片名的前缀
    private byte[] iconByte;//图标字节数组

    public LocalIcon(String iconTxt, byte[] iconByte){
        this.iconTxt = iconTxt;
        this.iconByte = iconByte;
    }

    public String getIconTxt() {
        return iconTxt;
    }

    public void setIconTxt(String iconTxt) {
        this.iconTxt = iconTxt;
    }

    public byte[] getIconByte() {
        return iconByte;
    }

    public void setIconByte(byte[] iconByte) {
        this.iconByte = iconByte;
    }


}
