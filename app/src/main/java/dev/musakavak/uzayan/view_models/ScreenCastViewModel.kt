package dev.musakavak.uzayan.view_models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import dev.musakavak.uzayan.models.ScreencastConfig
import dev.musakavak.uzayan.socket.Emitter
import java.net.DatagramSocket

@UnstableApi
class ScreenCastViewModel : ViewModel() {
    private var isInitiated = false
    private var isPlaying = false
    var exoPlayer by mutableStateOf<ExoPlayer?>(null)

    fun init(context: Context) {
        if (isInitiated) return
        val loadControl = DefaultLoadControl
            .Builder()
            .setBackBuffer(0, false)
            .setBufferDurationsMs(0, 10, 0, 0)
            .build()

        exoPlayer = ExoPlayer
            .Builder(context)
            .setLoadControl(loadControl)
            .build()

        isInitiated = true
    }

    fun start(screenName: String, width: String, height: String, x: String, y: String) {
        if (isPlaying) return
        val udp = DatagramSocket()
        val port = udp.localPort
        udp.close()

        val mediaItem = MediaItem.fromUri("udp://0.0.0.0:$port")

        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
        isPlaying = true

        Emitter.emit(
            "Screencast", ScreencastConfig(
                screenName,
                width,
                height,
                x,
                y,
                port.toString()
            )
        )
    }

    override fun onCleared() {
        exoPlayer?.release()
        super.onCleared()
    }
}