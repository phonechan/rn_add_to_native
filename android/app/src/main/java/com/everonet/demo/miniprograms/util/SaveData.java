package com.everonet.demo.miniprograms.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.everonet.demo.miniprograms.App;

/**
 * 保存信息
 * Created by wanny-n1 on 2017/1/10.
 */

public class SaveData {
    private static volatile SaveData INSTANCE;
    /**
     * 保存在手机里面的文件名
     */
    private static final String FILE_NAME = "EvoCashier";

    public static SaveData getInstance() {
        if (null == INSTANCE) {
            synchronized (SaveData.class) {
                if (null == INSTANCE) {
                    INSTANCE = new SaveData();
                }
            }
        }
        return INSTANCE;
    }

    private SharedPreferences getPref(@NonNull Context context) {
        Context ctx = context != null ? context : App.instance;
        return ctx.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor(@NonNull Context context) {
        Context ctx = context != null ? context : App.instance;
        return getPref(ctx).edit();
    }

    /**
     * 小程序版本号version
     *
     * @param context
     * @param version
     */

    public void saveMiniVersion(@NonNull Context context, String key, int version) {
        getEditor(context).putInt(key, version).apply();
    }

    /**
     * 小程序版本号version
     *
     * @param context
     * @return
     */
    public int getMiniVersion(@NonNull Context context, String key) {
        return getPref(context).getInt(key, 0);
    }


    static class Key {
        /**
         * 用户信息
         */
//        static final String MINI_VERSION = "version";

    }
}
