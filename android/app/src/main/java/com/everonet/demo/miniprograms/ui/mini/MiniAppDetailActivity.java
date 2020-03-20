package com.everonet.demo.miniprograms.ui.mini;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.everonet.demo.miniprograms.R;
import com.everonet.demo.miniprograms.api.ApiClient;
import com.everonet.demo.miniprograms.api.DefaultCallback;
import com.everonet.demo.miniprograms.model.MiniAppRespone;

public class MiniAppDetailActivity extends AppCompatActivity {
    private static final String TAG = MiniAppDetailActivity.class.getSimpleName();

    public static final String GID = "gid";
    private String gid;
    private MiniAppRespone.ResultEntity miniAppRespone = new MiniAppRespone.ResultEntity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miniapp_detail);
        gid = getIntent().getStringExtra(GID);
        if (TextUtils.isEmpty(gid)) {
            finish();
            return;
        }
        getMiniAppDetail();
    }

    private void getMiniAppDetail() {
//        showWaitingDialog(UIUtils.getString(R.string.please_wait));

        ApiClient.getAPI().miniAppDetail(gid).enqueue(new DefaultCallback<MiniAppRespone>(this) {

            @Override
            public void onResponse(@NonNull Context ctx, MiniAppRespone data) {
                if (data != null) {
                    if (data.getMini_app() != null) {
                        miniAppRespone = data.getMini_app();
                    }
//                        hideWaitingDialog();
                } else {
                    uploadError(null);
                }
            }

            @Override
            public void onFailure(@NonNull Context ctx, String error) {
                uploadError(null);
            }
        });
    }

    private void uploadError(Throwable throwable) {
//        if (throwable != null)
//            LogUtils.sf(throwable.getLocalizedMessage());
//        hideWaitingDialog();
        Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT);
    }
}
