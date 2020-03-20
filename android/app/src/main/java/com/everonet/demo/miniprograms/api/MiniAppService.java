package com.everonet.demo.miniprograms.api;

import com.everonet.demo.miniprograms.model.MiniAppRespone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface MiniAppService {

    @GET("api/v0/miniappinstores/")
    Call<MiniAppRespone> miniAppList();

    @GET("api/v0/miniappinstores/{gid}")
    Call<MiniAppRespone> miniAppDetail(@Path("gid") String sid);

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
