package com.everonet.demo.miniprograms.util;

import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Response;

/**
 * {@code Gson}工具类
 *
 * @author jin
 */
public class GsonUtils {
    /**
     * 将{@code src}序列化为{@code json}字符串
     *
     * @param src 待序列化对象
     * @return {@code json}字符串
     */
    public static String toJson(Object src) {
        try {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create();
            return gson.toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将{@code json}字符串反序列化为{@code T}对应的对象
     *
     * @param json     {@code json}字符串
     * @param classOfT 反序列化的目标{@code class}
     * @param <T>      反序列化的目标对象
     * @return 反序列化的目标对象
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                    .create();
            return gson.fromJson(TextUtils.isEmpty(json) ? json : json.trim(), classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将{@code json}的{#code response}反序列化为{@code T}对应的对象
     *
     * @param response
     * @param classOfT
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T fromResponse(Response response, Class<T> classOfT) throws IOException {
        String json = null;
        if (response != null && response.body() != null) {
            json = response.body().string();
        }
        return fromJson(TextUtils.isEmpty(json) ? json : json.trim(), classOfT);
    }

    /**
     * 将{@code json}的{#code response}反序列化为{@code T}对应的对象
     *
     * @param response
     * @param classOfT
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T fromResponse(retrofit2.Response response, Class<T> classOfT) throws IOException {
        String json = null;
        if (response != null && response.body() != null) {
            json = response.body().toString();
        }
        return fromJson(TextUtils.isEmpty(json) ? json : json.trim(), classOfT);
    }
}
