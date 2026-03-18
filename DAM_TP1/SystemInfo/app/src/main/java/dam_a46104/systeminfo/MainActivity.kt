package dam_a46104.systeminfo

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * ## System Info App — Secção 5.3
 *
 * Exibe informações do dispositivo Android lendo as propriedades
 * estáticas do objeto [android.os.Build].
 *
 * @author a46104
 * @since 2026-03-06
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvInfo = findViewById<TextView>(R.id.tv_system_info)

        val info = buildString {
            appendLine("=== Device Information ===")
            appendLine()
            appendLine("MANUFACTURER : ${Build.MANUFACTURER}")
            appendLine("BRAND        : ${Build.BRAND}")
            appendLine("MODEL        : ${Build.MODEL}")
            appendLine("DEVICE       : ${Build.DEVICE}")
            appendLine("PRODUCT      : ${Build.PRODUCT}")
            appendLine("HARDWARE     : ${Build.HARDWARE}")
            appendLine()
            appendLine("=== Software ===")
            appendLine()
            appendLine("ANDROID VER  : ${Build.VERSION.RELEASE}")
            appendLine("SDK INT      : ${Build.VERSION.SDK_INT}")
            appendLine("CODENAME     : ${Build.VERSION.CODENAME}")
            appendLine("BUILD ID     : ${Build.ID}")
            appendLine("FINGERPRINT  : ${Build.FINGERPRINT}")
        }

        tvInfo.text = info

        println(getString(R.string.activity_oncreate_msg, this@MainActivity.localClassName))
    }
}