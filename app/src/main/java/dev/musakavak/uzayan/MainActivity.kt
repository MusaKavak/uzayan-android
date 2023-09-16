package dev.musakavak.uzayan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.musakavak.uzayan.components.AllowListColumn
import dev.musakavak.uzayan.components.ConnectionStateCard
import dev.musakavak.uzayan.components.RemoteCommandsCard
import dev.musakavak.uzayan.components.ScreenCastLauncher
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.services.UzayanForegroundService
import dev.musakavak.uzayan.ui.theme.UzayanTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        readUriAndStartService()

        val padding = 16.dp
        setContent {
            val bottomSheetState = rememberModalBottomSheetState()
            val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState)
            val (sheetContent, setSheetC) = remember { mutableStateOf("") }

            val scope = rememberCoroutineScope()

            val setSheetContent: (String) -> Unit = {
                setSheetC(it)
                scope.launch { bottomSheetState.expand() }
            }

            UzayanTheme {
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetContainerColor = MaterialTheme.colorScheme.surface,
                    sheetPeekHeight = 0.dp,
                    sheetContent = {
                        when (sheetContent) {
                            "commands" -> {
                                RemoteCommandsCard(padding)
                            }

                            "screencast" -> {
                                ScreenCastLauncher()
                            }
                        }
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ConnectionStateCard(padding, ::startService, setSheetContent)
                            Spacer(Modifier.padding(padding))
                            val sp = getSharedPreferences("uzayan_allow_list", Context.MODE_PRIVATE)
                            AllowListColumn(padding, AllowListManager(sp))
                        }
                    }
                }
            }
        }
    }

    private fun readUriAndStartService() {
        intent.data?.let {
            val ip = it.getQueryParameter("ip")
            val port = it.getQueryParameter("port")?.toIntOrNull()
            val code = it.getQueryParameter("code")?.toIntOrNull()
            val secure = it.getQueryParameter("secure")?.toBooleanStrictOrNull()

            startService(ip, port, code, secure)
        }
    }

    private fun startService(
        ip: String?,
        port: Int?,
        code: Int?,
        secure: Boolean?
    ) {
        if (ip != null && port != null && code != null && secure != null) {
            stopService(Intent(this, UzayanForegroundService::class.java))

            val intent = Intent(this, UzayanForegroundService::class.java).apply {
                putExtra("u-ip", ip)
                putExtra("u-port", port)
                putExtra("u-code", code)
                putExtra("u-secure", secure)
            }

            startForegroundService(intent)
        }
    }
}

