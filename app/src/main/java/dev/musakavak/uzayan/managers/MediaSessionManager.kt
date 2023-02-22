package dev.musakavak.uzayan.managers

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.Rating
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Base64
import dev.musakavak.uzayan.models.MediaSession
import dev.musakavak.uzayan.models.MediaSessionState
import dev.musakavak.uzayan.services.NLService
import dev.musakavak.uzayan.socket.UdpSocket
import java.io.ByteArrayOutputStream

class MediaSessionManager(context: Context) {
    private val manager: MediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val componentName = ComponentName(context, NLService::class.java)

    private data class PreviousController(
        val controller: MediaController, val callback: MediaController.Callback, val token: String
    )

    private var currentControllers: MutableList<PreviousController> = mutableListOf()

    fun listen() {
        listenControllers(manager.getActiveSessions(componentName))
        manager.addOnActiveSessionsChangedListener({
            listenControllers(it)
        }, componentName)
    }

    private fun listenControllers(controllers: List<MediaController>?) {
        controllers?.let { newControllers ->
            currentControllers.forEach { previousControllers ->
                previousControllers.controller.unregisterCallback(previousControllers.callback)
            }
            currentControllers.clear()
            newControllers.forEach { controller ->
                val callback = createCallback(controller)
                currentControllers.add(
                    PreviousController(
                        controller,
                        callback,
                        controller.sessionToken.hashCode().toString()
                    )
                )
                controller.registerCallback(callback)
            }
        }
        emitMediaSessions(controllers)
    }

    fun sendCurrentSessions() {
        emitMediaSessions(currentControllers.map { it.controller }.toList())
    }

    fun mediaSessionControl(token: String, action: String, value: Any) {
        val session = currentControllers.find { it.token == token }
        session?.controller?.transportControls?.let {
            when (action) {
                "pause" -> it.pause()
                "play" -> it.play()
                "skipToNext" -> it.skipToNext()
                "skipToPrevious" -> it.skipToPrevious()
                "rateThumbs" -> it.setRating(Rating.newThumbRating(value as Boolean))
                "rateHeart" -> it.setRating(Rating.newHeartRating(value as Boolean))
                "rateClear" -> it.setRating(Rating.newUnratedRating(session.controller.ratingType))
                "seekTo" -> it.seekTo((value as String).toLong())
            }
        }
    }

    private fun createCallback(controller: MediaController): MediaController.Callback {
        return object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) {
                val rating = controller.metadata?.getRating(MediaMetadata.METADATA_KEY_USER_RATING)
                state?.let {
                    UdpSocket.emit(
                        "MediaSessionState", MediaSessionState(
                            controller.sessionToken.hashCode().toString(),
                            state.state == 3,
                            controller.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION),
                            state.position,
                            rating?.isRated,
                            rating?.hasHeart(),
                            rating?.isThumbUp,
                        )
                    )
                }
            }

            override fun onMetadataChanged(metadata: MediaMetadata?) {
                UdpSocket.emit("SingleMediaSession", createSessionObject(controller))
            }
        }
    }

    private fun emitMediaSessions(controllers: List<MediaController>?) {
        val list: MutableList<MediaSession> = mutableListOf()
        controllers?.forEach { list.add(createSessionObject(it)) }
        UdpSocket.emit("MediaSessions", list)
    }

    private fun createSessionObject(controller: MediaController): MediaSession {
        val ratingType = when (controller.ratingType) {
            0 -> null
            1 -> "heart"
            2 -> "thumbs"
            else -> null
        }
        return MediaSession(
            getBase64(controller.metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)),
            controller.metadata?.getText(MediaMetadata.METADATA_KEY_ARTIST).toString(),
            controller.metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM).toString(),
            controller.metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION),
            controller.playbackState?.state == 3,
            controller.packageName,
            controller.playbackState?.position,
            ratingType,
            controller.metadata?.getText(MediaMetadata.METADATA_KEY_TITLE).toString(),
            controller.sessionToken.hashCode().toString()
        )

    }

    private fun getBase64(bitmap: Bitmap?): String? {
        bitmap?.let {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
        return null
    }

}

