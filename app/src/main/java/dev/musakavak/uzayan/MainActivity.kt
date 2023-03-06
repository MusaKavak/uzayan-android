package dev.musakavak.uzayan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import dev.musakavak.uzayan.services.UzayanForegroundService
import dev.musakavak.uzayan.socket.Client
import dev.musakavak.uzayan.ui.theme.UzayanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            it
//        }
//        permission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        startForeground()
        setContent {
            UzayanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val urlArgs = intent.data
                        urlArgs?.let {
                            val ip = it.getQueryParameter("ip")
                            val port = it.getQueryParameter("port")?.toIntOrNull()
                            val code = it.getQueryParameter("code")
                            if (ip != null && port != null && code != null) {
                                Client.initializeClient(ip, port,true,code)
                                Text(text = "Pair Request Sent")
                            } else {
                                Text(text = "Some Error Occurred")
                            }
                        }
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
}

