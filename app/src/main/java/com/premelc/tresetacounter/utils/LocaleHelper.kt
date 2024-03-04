package com.premelc.tresetacounter.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import com.premelc.tresetacounter.data.PreferencesManager
import java.util.Locale

object LocaleHelper {
    fun updateResources(context: Context): Context =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context
        } else {
            val resources: Resources = context.resources
            val config = Configuration(resources.configuration)
            val preferences = PreferencesManager(context)
            val lang = preferences.getData("selected_language", "en")
            val locale = Locale(lang)
            config.setLocale(locale)
            context.createConfigurationContext(config)
        }
}
