package dev.musakavak.uzayan.managers

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Base64
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
                emitMediaSessions(it)
            }
        }
    }
    private fun createCallback(controller: MediaController): MediaController.Callback {
        return object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) {
                super.onPlaybackStateChanged(state)
                Emitter.emitSingleMediaSession(createSessionObject(controller))
            }
        }
    }

    private fun emitMediaSessions(controllers: List<MediaController>) {
        val list: MutableList<MediaSession> = mutableListOf()
        controllers.forEach { list.add(createSessionObject(it)) }
        if (list.isNotEmpty()) Emitter.emitMediaSessions(list)
    }

    private fun createSessionObject(controller: MediaController): MediaSession {
        return MediaSession(
            getBase64(controller.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)),
            controller.metadata?.getText(MediaMetadata.METADATA_KEY_ARTIST).toString(),
            controller.metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM).toString(),
            controller.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION),
            controller.packageName,
            controller.metadata?.getText(MediaMetadata.METADATA_KEY_TITLE).toString(),
            controller.sessionToken.hashCode()
        )
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

}

