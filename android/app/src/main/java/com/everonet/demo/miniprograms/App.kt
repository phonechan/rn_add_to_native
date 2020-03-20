package com.everonet.demo.miniprograms

import android.app.Application
import android.content.Context
import com.facebook.soloader.SoLoader

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
        instance = this
    }

    private var instance: App? = null

    fun getInstance(): App? {
        return instance
    }

    fun getAppContext(): Context? {
        return instance
    }

}