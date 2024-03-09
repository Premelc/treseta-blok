package com.premelc.tresetacounter.utils

import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
internal fun WakeLock() {
    val context = LocalContext.current
    val powerManager = context.getSystemService(PowerManager::class.java)
    val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyApp:WakeLockTag")

    DisposableEffect(Unit) {
        wakeLock.acquire(10 * 60 * 1000L)

        onDispose {
            wakeLock.release()
        }
    }
}
