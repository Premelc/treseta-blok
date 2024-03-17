package com.premelc.tresetacounter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.premelc.tresetacounter.navigation.NavigationHost
import com.premelc.tresetacounter.ui.theme.TresetaBlokTheme
import com.premelc.tresetacounter.utils.LocaleHelper.updateResources

private val testDevices = listOf(
    // S22 Ultra
    "B428003909222CD446B1AD911C12D060",
    // A52s
    "E5FAE5B74F63A36F1E3F7831CAB8015F",
    // for emulators
    AdRequest.DEVICE_ID_EMULATOR
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TresetaBlokTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationHost()
                }
            }
        }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
        )
        MobileAds.initialize(this) {}
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(updateResources(base!!))
    }
}
