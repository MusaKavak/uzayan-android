package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.ConnectionObject
import dev.musakavak.uzayan.models.MediaSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Emitter {
    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)

        fun emitMediaSessions(mediaSessions: List<MediaSession>) {
            emit(ConnectionObject("MediaSessions", mediaSessions))
        }

        fun emitSingleMediaSession(mediaSession: MediaSession) {
            emit(ConnectionObject("SingleMediaSession", mediaSession))
        }

        private fun <T> emit(emitObject: ConnectionObject<T>) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    val stringToEmit = Gson().toJson(emitObject)
                    UdpSocket.sendMessage(stringToEmit)
                }
            }
        }
    }
}