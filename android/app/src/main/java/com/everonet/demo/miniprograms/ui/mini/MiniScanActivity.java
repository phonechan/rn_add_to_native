/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.everonet.demo.miniprograms.ui.mini;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.everonet.demo.miniprograms.R;
import com.everonet.demo.miniprograms.util.ScanBoxView;
import com.everonet.demo.miniprograms.zxing.camera.CameraManager;
import com.everonet.demo.miniprograms.zxing.utils.BeepManager;
import com.everonet.demo.miniprograms.zxing.utils.CaptureActivityHandler;
import com.everonet.demo.miniprograms.zxing.utils.InactivityTimer;
import com.google.zxing.Result;
import com.everonet.demo.miniprograms.zxing.decode.*;

import java.io.IOException;


/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class MiniScanActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    public static final int CODE_SCAN_MINI_GID = 0x01;

    private static final String TAG = MiniScanActivity.class.getSimpleName();
    public static final String REQUEST_CODE = "request_code";

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private ScanBoxView scanCropView;
    private TextView mScanAmount;
    private TextView mScanContent;
    private int mRequestCode = 0;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private String mOrderNumber;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private ImageView mCloseImg;
    private View mHintPopView;
    private boolean isPay = false;
    private String paymentBrand;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanCropView = (ScanBoxView) findViewById(R.id.capture_crop_view_v);
        mScanAmount = (TextView) findViewById(R.id.capture_charge_amount);
        mScanContent = (TextView) findViewById(R.id.capture_content);
        mCloseImg = (ImageView) findViewById(R.id.capture_close);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        mRequestCode = getIntent().getIntExtra(REQUEST_CODE, 0);
        switch (mRequestCode) {
            case CODE_SCAN_MINI_GID:
                mScanAmount.setVisibility(View.VISIBLE);
                mScanContent.setVisibility(View.VISIBLE);
                mScanContent.setText(R.string.scan);
                break;
            default:
                mScanAmount.setVisibility(View.GONE);
                mScanContent.setVisibility(View.GONE);
                break;
        }

        mCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiniScanActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is
        // necessary because we don't
        // want to open the camera driver and measure the screen size if we're
        // going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.react_enter, R.anim.react_exit);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        switch (mRequestCode) {
            case CODE_SCAN_MINI_GID:
                pay(rawResult.getText());
                break;
            default:
                break;
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {

        //TODO  相机权限检测

        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }

        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera" + e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
//        showHintPop(R.string.alert_not_support_camera);
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;
        mCropRect = scanCropView.getScanBoxAreaRect(cameraWidth, cameraHeight);
    }


    private void pay(String scanCodeId) {
        if (TextUtils.isEmpty(scanCodeId)) {
            finish();
            return;
        }
        startDetailActivity(scanCodeId);
        Log.i(TAG, "pay: " + scanCodeId);
    }

    private void startDetailActivity(String gid) {
        Intent mIntent = new Intent(this, MiniAppDetailActivity.class);
        mIntent.putExtra(MiniAppDetailActivity.GID, gid);
        startActivity(mIntent);
        overridePendingTransition(R.anim.react_enter, R.anim.react_exit);
        finish();
    }

}