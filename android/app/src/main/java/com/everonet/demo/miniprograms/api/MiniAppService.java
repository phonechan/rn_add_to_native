package com.everonet.demo.miniprograms.api;

import com.everonet.demo.miniprograms.model.MiniAppRespone;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MiniAppService {

    @GET("api/v0/miniappinstores/")
    Call<MiniAppRespone> miniAppList();

    @GET("api/v0/miniappinstores/{gid}")
    Call<MiniAppRespone> miniAppDetail(@Path("gid") String sid);
}
