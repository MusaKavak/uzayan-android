package dev.musakavak.uzayan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import dev.musakavak.uzayan.ui.theme.UzayanTheme
import dev.musakavak.uzayan.view_models.ScreenCastViewModel

@UnstableApi
class ScreenCastActivity : ComponentActivity() {
    private lateinit var vm: ScreenCastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UzayanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    vm = viewModel(ScreenCastViewModel::class.java)
                    ScreenCastPlayer()
                }
            }
        }
    }

    @Composable
    private fun ScreenCastPlayer() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AndroidView(
                factory = {
                    vm.init(it)
                    PlayerView(it).apply {
                        player = vm.exoPlayer
                    }
                }
            ) { startPlayer() }
        }
    }

    private fun startPlayer() {
        vm.start(
            intent.getStringExtra("screen_name")!!,
            intent.getStringExtra("width")!!,
            intent.getStringExtra("height")!!,
            intent.getStringExtra("x")!!,
            intent.getStringExtra("y")!!
        )
    }
}
