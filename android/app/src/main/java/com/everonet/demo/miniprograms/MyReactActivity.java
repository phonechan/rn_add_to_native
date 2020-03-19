package com.everonet.demo.miniprograms;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.oblador.vectoricons.VectorIconsPackage;

import java.io.File;

public class MyReactActivity extends Activity implements DefaultHardwareBackBtnHandler {

    private static final String TAG = MyReactActivity.class.getSimpleName();

    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;

    public static final String EXTRA_APP_ID = "EXTRA_APP_ID";
    public static final String EXTRA_MODULE_NAME = "EXTRA_MODULE_NAME";
    private String mMiniAppId;
    private String mModuleName;

    public static void startActivity(Activity activity, String appId, String moduleName) {
        Intent intent = new Intent(activity, MyReactActivity.class);
        intent.putExtra(MyReactActivity.EXTRA_APP_ID, appId);
        intent.putExtra(MyReactActivity.EXTRA_MODULE_NAME, moduleName);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.react_enter, R.anim.react_exit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMiniAppId = getIntent().getStringExtra(EXTRA_APP_ID);
        mModuleName = getIntent().getStringExtra(EXTRA_MODULE_NAME);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "cil_react" + File.separator + mMiniAppId + "/index.android.bundle";

        Log.i(TAG, path);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("index.android.bundle")
                .setJSBundleFile(path)
                .setJSMainModulePath("index")
                .addPackage(new MainReactPackage())
                .addPackage(new VectorIconsPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // The string here (e.g. "MyReactNativeApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        mReactRootView.startReactApplication(mReactInstanceManager, mModuleName, null);


        setContentView(R.layout.activity_flutter_container);

        FrameLayout container = findViewById(R.id.container);
        container.addView(mReactRootView);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
        }
    }

    @Override
    public void onBackPressed() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
        overridePendingTransition(0, R.anim.react_exit);

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
