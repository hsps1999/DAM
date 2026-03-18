package dam_a46104.sketch2art.util

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object BitmapUtils {
    fun toBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        // Use NO_WRAP to ensure the Base64 string doesn't contain newlines, which can break some APIs
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
