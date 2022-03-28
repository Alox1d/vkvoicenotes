package com.alox1d.vkvoicenotes

import android.app.Application
import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.di.AppComponent
import com.alox1d.vkvoicenotes.di.DaggerAppComponent

class App : Application() {
    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.invoke(this)
        daggerAppComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }
    companion object {
        lateinit var instance: App
        lateinit var database: AppDatabase
        lateinit var daggerAppComponent: AppComponent
    }
}