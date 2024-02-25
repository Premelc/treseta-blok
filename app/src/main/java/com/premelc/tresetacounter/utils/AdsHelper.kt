import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.RequestConfiguration
import com.premelc.tresetacounter.BuildConfig
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

object AdsHelper {
    fun createRequest(context: Context) {
        val adRequest =
            RequestConfiguration.Builder().setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
        if (BuildConfig.DEBUG) {
            val deviceId = md5(getDeviceId(context))
            if (!TextUtils.isEmpty(deviceId)) {
                val id = deviceId!!.uppercase(Locale.getDefault())
                Log.i("premoDebug", "id: $id")
                adRequest.setTestDeviceIds(listOf(id, AdRequest.DEVICE_ID_EMULATOR)).build()
            }
        }
    }

    private fun md5(md5: String): String? {
        if (TextUtils.isEmpty(md5)) return null
        try {
            val md = MessageDigest.getInstance("MD5")
            val array = md.digest(md5.toByteArray(charset("UTF-8")))
            val sb = StringBuilder()
            for (anArray in array) {
                sb.append(Integer.toHexString(anArray.toInt() and 0xFF or 0x100).substring(1, 3))
            }
            return sb.toString()
        } catch (ignored: NoSuchAlgorithmException) {
        } catch (ignored: UnsupportedEncodingException) {
        }
        return null
    }

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
