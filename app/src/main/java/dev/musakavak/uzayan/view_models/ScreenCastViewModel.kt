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
    private var isPlaying = false
    var isInitiated = false
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
        isPlaying = true
        val udp = DatagramSocket()
        val port = udp.localPort
        udp.close()

        val mediaItem = MediaItem.fromUri("udp://0.0.0.0:$port")

        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()

        Emitter.emit(
            "StartScreencast", ScreencastConfig(
                screenName,
                width,
                height,
                x,
                y,
                port.toString()
            )
        )
    }

    fun stop() {
        exoPlayer?.stop()
        Emitter.emit("StopScreencast", null)
        isPlaying = false
    }

    override fun onCleared() {
        exoPlayer?.release()
        super.onCleared()
    }

}