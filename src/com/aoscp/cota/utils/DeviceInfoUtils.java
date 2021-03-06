package com.aoscp.cota.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DeviceInfoUtils {
    private static final String MOD_VERSION = "ro.modversion";
	private static final String AOSCP_VERSION = "ro.aoscp.version";
    private static final String PROPERTY_DEVICE = "ro.product.model";
    private static final String PROPERTY_DEVICE_EXT = "ro.product.device";

    public static String getDate() {
        return new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(System
                .currentTimeMillis()));
    }

    public static String getDevice() {
        String device = UpdateUtils.getProp(PROPERTY_DEVICE);
        if (device == null || device.isEmpty()) {
            device = UpdateUtils.getProp(PROPERTY_DEVICE_EXT);
        }
        return device == null ? "" : device;
    }

    public static String getExplicitVersion() {
        return UpdateUtils.getProp(MOD_VERSION);
    }
	
	public static String getAoscpVersion() {
        return UpdateUtils.getProp(AOSCP_VERSION);
    }

    public static String getReadableDate(String fileDate) {
        try {
            Date currentDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date parsedDate = format.parse(fileDate);
            long diff = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - parsedDate.getTime());
            return diff > 1 ? diff + " days ago" : "today";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
