package com.everonet.demo.miniprograms

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.facebook.soloader.SoLoader
import com.facebook.stetho.Stetho

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
        instance = this

        Stetho.initializeWithDefaults(this)
    }

    private var instance: App? = null

    fun getInstance(): App? {
        return instance
    }

    fun getAppContext(): Context? {
        return instance
    }

}