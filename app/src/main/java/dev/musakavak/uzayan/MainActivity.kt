package dev.musakavak.uzayan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.musakavak.uzayan.components.AllowListColumn
import dev.musakavak.uzayan.components.ConnectionStateCard
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.services.UzayanForegroundService
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
        val padding = 16.dp
        setContent {
            UzayanTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(padding),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ConnectionStateCard(intent.data, getDeviceName())
                        Spacer(Modifier.padding(padding))
                        val sp = getSharedPreferences("uzayan_allow_list", Context.MODE_PRIVATE)
                        AllowListColumn(padding, AllowListManager(sp))
                    }
                }
            }
        }
    }

    private fun startForeground() {
        val intent = Intent(this, UzayanForegroundService::class.java)
        startForegroundService(intent)
    }

    private fun getDeviceName(): String {
        return Settings.Global.getString(contentResolver, Settings.Global.DEVICE_NAME)
    }
}

