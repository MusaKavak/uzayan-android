package dev.musakavak.uzayan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.musakavak.uzayan.managers.UdpSocketManager
import dev.musakavak.uzayan.services.UzayanForegroundService
import dev.musakavak.uzayan.ui.theme.UzayanTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForeground()
        setContent {
            UzayanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    Home()
                }
            }
        }
    }

    @Composable
    @Preview
    fun Home() {
        val scope = rememberCoroutineScope()

        val sendAddress: MutableState<String> = remember { mutableStateOf("Address") }
        val message: MutableState<String> = remember { mutableStateOf("Message") }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(value = sendAddress.value, onValueChange = { sendAddress.value = it })
            TextField(value = message.value, onValueChange = { message.value = it })
            Button(onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        UdpSocketManager.sendMessage(message.value, sendAddress.value)
                    }
                }
            }) {
                Text(text = "Send")
            }
        }
    }

    private fun startForeground() {
        val intent = Intent(this, UzayanForegroundService::class.java)
        startForegroundService(intent)
    }


//    @Preview(showBackground = false)
//    @Composable
//    fun DefaultPreview() {
//        UzayanTheme {
//            Greeting("Android")
//        }
//    }
}
