package me.nereo.multi_image_selector.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import me.nereo.multi_image_selector.bean.WaterMarkBean;

/**
 * Created by stf on 2018-08-14.
 */

public class Config {
    public static WaterMarkBean getWaterMarkBean() {
        WaterMarkBean markBean = new WaterMarkBean();
        markBean.setMark(getDate());
        markBean.setAlpha(180);
        markBean.setAntiAlias(true);
        markBean.setRotate(-20);
        markBean.setColor("#f4ea2a");
        markBean.setTextSize(25);
        return markBean;
    }

    private static String getDate() {
        SimpleDateFormat dff = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String date = "";
        try {
            dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            date = dff.format(new Date());
        } catch (Exception e) {
            e.fillInStackTrace();
            long currentTime = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            date = formatter.format(currentTime);
        }
        return date;
    }
}
