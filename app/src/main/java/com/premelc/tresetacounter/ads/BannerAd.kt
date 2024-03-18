package com.premelc.tresetacounter.ads

import android.util.Log
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val config = LocalConfiguration.current
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 60.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        config.screenWidthDp
                    )
                )
                Log.i("premoDebug", AdRequest.Builder().build().isTestDevice(context).toString())
                // test ad unit id
                // adUnitId = "ca-app-pub-3940256099942544/6300978111"
                // this one is the real one
                adUnitId = "ca-app-pub-9680718533779299/4280350693"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
