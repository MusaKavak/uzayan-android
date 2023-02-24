package dev.musakavak.uzayan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import dev.musakavak.uzayan.socket.UdpSocket
import dev.musakavak.uzayan.services.UzayanForegroundService
import dev.musakavak.uzayan.ui.theme.UzayanTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                            val code = it.getQueryParameter("code")

                            if (ip != null && code != null) {
                                UdpSocket.setAddress(ip)
                                UdpSocket.emit("Pair", code)
                                Text(text = "Pair Request Sent")
                            } else {
                                Text(text = "Query Parameters Not Correct")
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
        val scope = rememberCoroutineScope()
        val message: MutableState<String> = remember { mutableStateOf("Message") }
        val address: MutableState<String> = remember { mutableStateOf("192.168.1.") }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(value = message.value, onValueChange = { message.value = it })
            Button(onClick = {
                scope.launch {
                    withContext(Dispatchers.IO) {
                        UdpSocket.sendMessage(message.value)
                    }
                }
            }) {
                Text(text = "Send")
            }
            TextField(
                value = address.value,
                onValueChange = { address.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = {
                UdpSocket.setAddress(address.value)
            }) {
                Text(text = "Set Address")
            }
        }
    }

    private fun startForeground() {
        val intent = Intent(this, UzayanForegroundService::class.java)
        startForegroundService(intent)
    }

//    fun requestReadStorePermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                //Permission Granted
//                val x = 2
//            }
//            else -> requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        }
//    }
}
