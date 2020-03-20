package com.everonet.demo.miniprograms.api;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by wanny-n1 on 2017/1/3.
 */

public abstract class DefaultCallback<T> implements Callback<T> {
    private Context mCtx;

    public DefaultCallback(@NonNull Context ctx) {
        this.mCtx = ctx;
    }

    public DefaultCallback() {
    }

    @Override
    public void onResponse(Call<T> call, final Response<T> response) {
        try {

            if (response.isSuccessful()) {
                onResponse(mCtx, response.body());
            } else {
                onFailure(mCtx, response.message());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Call<T> call, final Throwable t) {
        try {
        } catch (Exception e1) {
            return;
        }
        if ((call != null && call.isCanceled()) || t == null || TextUtils.isEmpty(t.getMessage())) {
            return;
        }
        onFailure(mCtx, t.getMessage());
    }

    /**
     * 正确返回
     *
     * @param ctx
     * @param data
     */

    public abstract void onResponse(@NonNull Context ctx, T data);

    /**
     * 错误返回
     *
     * @param ctx
     * @param error
     */
    public abstract void onFailure(@NonNull Context ctx, String error);


}
