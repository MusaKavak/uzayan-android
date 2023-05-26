package dev.musakavak.uzayan

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.musakavak.uzayan.components.ConnectionStateCard
import dev.musakavak.uzayan.services.UzayanForegroundService
import dev.musakavak.uzayan.tools.PairTool
import dev.musakavak.uzayan.ui.theme.UzayanTheme

class MainActivity : ComponentActivity() {

//    private val request =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//            it.forEach { result ->
//                println("Key ${result.key} value ${result.value}")
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForeground()
        setContent {
            UzayanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ConnectionStateCard()
                        ConnectionCard()
                        Home()
                    }
                }
            }
        }
    }

    @Composable
    @Preview
    fun Home() {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Uzayan", Modifier.size(Dp(50F)))
        }
    }

    private fun startForeground() {
        val intent = Intent(this, UzayanForegroundService::class.java)
        startForegroundService(intent)
    }

    @Composable
    private fun ConnectionCard() {
        val urlArgs = intent.data
        urlArgs?.let {
            val ip = it.getQueryParameter("ip")
            val port = it.getQueryParameter("port")?.toIntOrNull()
            val code = it.getQueryParameter("code")
            val name = it.getQueryParameter("name")
            if (ip != null && port != null && code != null) {
                PairTool().sendPairRequest(ip, port, code, getDeviceName())
                Text(text = "Pair Request Sent To $name")
            } else {
                Text(text = "Some Error Occurred")
            }
        }
    }

    private fun getDeviceName(): String {
        return Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME)
    }
}

