package dev.musakavak.uzayan.managers

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Base64
import android.util.Log
import dev.musakavak.uzayan.models.MediaSession
import dev.musakavak.uzayan.services.NLService
import dev.musakavak.uzayan.socket.Emitter
import java.io.ByteArrayOutputStream

class MediaSessionManager(context: Context) {
    private val manager: MediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val componentName = ComponentName(context, NLService::class.java)

    private data class PreviousController(
        val controller: MediaController, val callback: MediaController.Callback
    )

    private var currentControllers: MutableList<PreviousController> = mutableListOf()

    fun listen() {
        listenControllers(manager.getActiveSessions(componentName))
        manager.addOnActiveSessionsChangedListener({
            listenControllers(it)
        }, componentName)
    }

    private fun listenControllers(controllers: List<MediaController>?) {
        currentControllers.forEach {
            it.controller.unregisterCallback(it.callback)
        }
        currentControllers.clear()
        controllers?.let {
            it.forEach { controller ->
                val callback = createCallback(controller)
                currentControllers.add(PreviousController(controller, callback))
                controller.registerCallback(callback)
            }
            if (it.isNotEmpty()) {
                createMediaControllerList(it)
            }
        }
    }

    private fun createMediaControllerList(controllers: List<MediaController>) {
        val list: MutableList<MediaSession> = mutableListOf()
        controllers.forEach {
            list.add(
                MediaSession(
                    getBase64(it.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)),
                    it.metadata?.getText(MediaMetadata.METADATA_KEY_ARTIST).toString(),
                    it.metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM).toString(),
                    it.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION),
                    it.packageName,
                    it.metadata?.getText(MediaMetadata.METADATA_KEY_TITLE).toString(),
                    it.sessionToken.hashCode()
                )
            )
        }
        if (list.isNotEmpty()) Emitter.emitMediaSessions(list)
    }

    private fun getBase64(bitmap: Bitmap?): String? {
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            val byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
        return null
    }

    private fun createCallback(controller: MediaController): MediaController.Callback {
        return object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) {
                super.onPlaybackStateChanged(state)

            }
        }
    }

    private val tag = "MediaSessionManager"
    fun printBundle(bundle: MediaMetadata?) {
        val extras = bundle?.keySet()
        Log.w(tag, "-------------------------------------------------")
        extras?.forEach {
            Log.i(tag, "Key: $it ----------- Value: ${bundle.getString(it)}")
        }
        Log.w(tag, "-------------------------------------------------")
    }
}

