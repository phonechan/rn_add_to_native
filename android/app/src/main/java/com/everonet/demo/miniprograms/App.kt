package com.everonet.demo.miniprograms

import android.annotation.SuppressLint
import androidx.multidex.MultiDexApplication
import com.facebook.soloader.SoLoader
import com.facebook.stetho.Stetho

class App : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
        instance = this

        Stetho.initializeWithDefaults(this)
    }
}