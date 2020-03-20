package com.everonet.demo.miniprograms.ui.mini;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.everonet.demo.miniprograms.R;
import com.everonet.demo.miniprograms.adapter.MiniListRecyclerViewAdapter;
import com.everonet.demo.miniprograms.api.ApiClient;
import com.everonet.demo.miniprograms.api.DefaultCallback;
import com.everonet.demo.miniprograms.model.MiniAppRespone;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MiniAppListActivity extends AppCompatActivity {
    private static final String TAG = MiniAppListActivity.class.getSimpleName();
    private List<MiniAppRespone.ResultEntity> mData = new ArrayList<>();
    private MiniListRecyclerViewAdapter mAdapter;
    @BindView(R.id.rvMiniApp)
    RecyclerView mRvMiniApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_miniapp);
        ButterKnife.bind(this);
        setAdapter();
        loadData();
    }


    private void loadData() {
        getMiniAppList();
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new MiniListRecyclerViewAdapter(this, mData);
            mRvMiniApp.setAdapter(mAdapter);

            mAdapter.setOnItemClickListener((v, position) -> {
                MiniAppRespone.ResultEntity resultEntity = mData.get(position);

                Intent mIntent = new Intent(this, MiniAppDetailActivity.class);
                mIntent.putExtra(MiniAppDetailActivity.GID, resultEntity.getGid());
                startActivity(mIntent);
            });
        }
        mRvMiniApp.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getMiniAppList() {
//        showWaitingDialog(UIUtils.getString(R.string.please_wait));

        ApiClient.getAPI().miniAppList().enqueue(new DefaultCallback<MiniAppRespone>(this) {

            @Override
            public void onResponse(@NonNull Context ctx, MiniAppRespone data) {
                if (data != null) {
                    if (data.getMini_apps() != null && data.getMini_apps().size() > 0) {
                        mData.clear();
                        mData.addAll(data.getMini_apps());
                        mAdapter.notifyDataSetChanged();
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


    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MiniAppListActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.react_enter, R.anim.react_exit);
    }
}
