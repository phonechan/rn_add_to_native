package com.everonet.demo.miniprograms.ui.mini;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.everonet.demo.miniprograms.App;
import com.everonet.demo.miniprograms.MyReactActivity;
import com.everonet.demo.miniprograms.R;
import com.everonet.demo.miniprograms.api.ApiClient;
import com.everonet.demo.miniprograms.api.DefaultCallback;
import com.everonet.demo.miniprograms.model.MiniAppRespone;
import com.everonet.demo.miniprograms.util.DownloadUtil;
import com.everonet.demo.miniprograms.util.FileUtils;
import com.everonet.demo.miniprograms.util.SaveData;
import com.everonet.demo.miniprograms.util.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class MiniAppDetailActivity extends AppCompatActivity {
    private static final String TAG = MiniAppDetailActivity.class.getSimpleName();

    public static final String GID = "gid";
    private String gid;
    private String moduleName;
    private int version;
    private MiniAppRespone.ResultEntity miniAppRespone = new MiniAppRespone.ResultEntity();

    private ProgressBar progress;

    public static void startActivity(Activity activity, String gid) {
        Intent intent = new Intent(activity, MiniAppDetailActivity.class);
        intent.putExtra(MiniAppDetailActivity.GID, gid);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.react_enter, R.anim.react_exit);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miniapp_detail);
        progress = findViewById(R.id.progress);
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
                        moduleName = miniAppRespone.getModule_name();
                        Log.d(TAG, "local version = " + SaveData.getInstance().getMiniVersion(ctx, gid));
                        Log.d(TAG, "new version = " + miniAppRespone.getVersion());
                        if (SaveData.getInstance().getMiniVersion(ctx, gid) < miniAppRespone.getVersion()) { //本地版本号 < 网络版本号
                            if (!TextUtils.isEmpty(miniAppRespone.getBundle_uri())) {
//                                downloadMiniProgramFileSync(miniAppRespone.getBundle_uri(), miniAppRespone.getGid());
                                downFile(miniAppRespone.getBundle_uri(), miniAppRespone.getGid(), miniAppRespone.getVersion());
                            }
                            version = miniAppRespone.getVersion();
                        } else {
                            Log.d(TAG, "Don not need download");
                            MyReactActivity.startActivity(MiniAppDetailActivity.this, gid, moduleName);
                            MiniAppDetailActivity.this.finish();
                        }
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

    /**
     * 根据详情中'bundle_uri'，下载文件
     */
    private void downloadMiniProgramFileSync(String url, String fileName) {
        new AsyncTask<Void, Long, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ApiClient.getDownAPI().downloadFile(url).enqueue(new DefaultCallback<ResponseBody>(App.instance) {
                    @Override
                    public void onResponse(@NonNull Context ctx, ResponseBody data) {
                        boolean writtenToDisk = writeResponseBodyToDisk(data, fileName);
                        Log.d(TAG, "file download is success " + writtenToDisk);
                    }

                    @Override
                    public void onFailure(@NonNull Context ctx, String error) {
                        Log.e(TAG, "file download is fail" + error);
                    }
                });
                return null;
            }
        }.execute();

    }

    public boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            Log.d(TAG, "writeResponseBodyToDisk: ");
            File futureStudioIconFile = FileUtils.createNewFile(App.instance, FileUtils.PATH_DOWNLOAD, fileName + "_" + version);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                // 解压缩
                String pathIn = App.instance.getExternalCacheDir() + FileUtils.PATH_DOWNLOAD + fileName + "_" + version;
                String pathOut = App.instance.getExternalCacheDir() + FileUtils.PATH_DOWNLOAD + fileName;
                ZipUtils.unzip(pathIn, pathOut);

                //文件下载到本地成功，新的version写到SP中
                SaveData.getInstance().saveMiniVersion(App.instance, gid, version);

                MyReactActivity.startActivity(this, gid, moduleName);
                MiniAppDetailActivity.this.finish();
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 文件下载
     */
    private void downFile(String url, String gid, int version) {
        DownloadUtil.get().download(url, App.instance.getExternalCacheDir() + FileUtils.PATH_DOWNLOAD, gid + "_" + version,
                new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        Log.i(TAG, "+++ onDownloadSuccess +++");
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        mHandler.sendMessage(msg);

                    }

                    @Override
                    public void onDownloading(int progress) {
                        Log.i(TAG, "+++ onDownloading +++" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 11;
                        msg.obj = progress;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        //下载异常进行相关提示操作
                        Log.i(TAG, "+++ onDownloadFailed +++");
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = e;
                        mHandler.sendMessage(msg);
                    }
                });

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 11:
                    Integer p = (Integer) msg.obj;
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(p);
                    break;
                case 0:
                    //下载完成进行相关逻辑操作
                    // 解压缩
                    String pathIn = App.instance.getExternalCacheDir() + FileUtils.PATH_DOWNLOAD + gid + "_" + version;
                    String pathOut = App.instance.getExternalCacheDir() + FileUtils.PATH_DOWNLOAD + gid;
                    try {

                        ZipUtils.unzip(pathIn, pathOut);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //文件下载到本地成功，新的version写到SP中
                    SaveData.getInstance().saveMiniVersion(App.instance, gid, version);

                    MyReactActivity.startActivity(MiniAppDetailActivity.this, gid, moduleName);
                    MiniAppDetailActivity.this.finish();
                    break;
                case 1:
                    MiniAppDetailActivity.this.finish();
                    break;
            }
        }
    };

}
