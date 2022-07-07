package com.alox1d.vkvoicenotes

import android.app.Application
import androidx.appcompat.content.res.AppCompatResources
import com.alox1d.vkvoicenotes.data.database.AppDatabase
import com.alox1d.vkvoicenotes.di.AppComponent
import com.alox1d.vkvoicenotes.di.DaggerAppComponent
import com.alox1d.vkvoicenotes.internal.VkSilentTokenExchangerImpl
import com.vk.api.sdk.VK
import com.vk.auth.internal.AuthLibBridge
import com.vk.auth.main.VkClientUiInfo
import com.vk.superapp.SuperappKit
import com.vk.superapp.SuperappKitConfig
import com.vk.superapp.core.SuperappConfig

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
        initSuperAppKit(this)
    }

    private fun initSuperAppKit(context: Application) {

        val appName = "VK Voice Notes"

        // Укажите этот параметр и appId в файле ресурсов!
        val clientSecret = context.getString(R.string.vk_client_secret)

        // Укажите иконку, которая будет отображаться в компонентах пользовательского интерфейса
        val icon = AppCompatResources.getDrawable(context, R.mipmap.ic_launcher)!!

        val appInfo = SuperappConfig.AppInfo(
            appName,
            VK.getAppId(context).toString(),
            BuildConfig.VERSION_NAME
        )
        val config = SuperappKitConfig.Builder(context)
            // настройка VK ID
            .setAuthModelData(clientSecret)
            .setAuthUiManagerData(VkClientUiInfo(icon, appName))
            .setLegalInfoLinks(
                serviceUserAgreement = "https://id.vk.com/terms",
                servicePrivacyPolicy = "https://id.vk.com/privacy"
            )
            .setApplicationInfo(appInfo)

            // Обмен Silent token на Access
            .setSilentTokenExchanger(VkSilentTokenExchangerImpl(context))
            .build()

        // Инициализация SuperAppKit
        SuperappKit.init(config)
    }

    companion object {
        lateinit var instance: App
        lateinit var database: AppDatabase
        lateinit var daggerAppComponent: AppComponent
    }
}