package dev.musakavak.uzayan

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import dev.musakavak.uzayan.ui.theme.UzayanTheme
import dev.musakavak.uzayan.view_models.ScreenCastViewModel
import kotlin.math.roundToInt

@UnstableApi
class ScreenCastActivity : ComponentActivity() {
    private lateinit var vm: ScreenCastViewModel
    private var isPlayerInitiated = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UzayanTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color.Black
                ) {
                    vm = viewModel(ScreenCastViewModel::class.java)
                    ScreenCastPlayer()
                }
            }
        }
        hideSystemUI()
    }

    @Composable
    private fun ScreenCastPlayer() {
        var cursorX by remember { mutableFloatStateOf(0f) }
        var cursorY by remember { mutableFloatStateOf(0f) }

        var videoSize by remember { mutableStateOf(IntSize.Zero) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { _, dragAmount ->
                            cursorX =
                                (cursorX + dragAmount.x).coerceIn(0F, videoSize.width.toFloat())
                            cursorY =
                                (cursorY + dragAmount.y).coerceIn(0F, videoSize.height.toFloat())
                        },
                        onDragEnd = {
                            vm.mouseMove(cursorX, cursorY, videoSize)
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            vm.mouseClick(true)
                        },
                        onLongPress = {
                            vm.mouseClick(false)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box {
                AndroidView(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        videoSize = coordinates.size
                    },
                    factory = {
                        vm.init(it)
                        PlayerView(it).apply {
                            player = vm.exoPlayer
                            useController = false
                        }
                    },
                ) {
                    startPlayer()
                    isPlayerInitiated = true
                }

                Image(
                    modifier = Modifier
                        .offset {
                            IntOffset(cursorX.roundToInt(), cursorY.roundToInt())
                        },
                    painter = painterResource(R.drawable.cursor),
                    contentDescription = ""
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPlayerInitiated) {
            startPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        vm.stop()
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

    private fun hideSystemUI() {
        actionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
}
