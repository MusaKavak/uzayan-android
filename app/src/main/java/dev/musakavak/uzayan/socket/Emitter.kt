package dev.musakavak.uzayan.socket

import com.google.gson.Gson
import dev.musakavak.uzayan.models.NetworkMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.PrintWriter

class Emitter {
    companion object {
        private val gson = Gson()
        private val scope = CoroutineScope(Dispatchers.IO)

        var writer: PrintWriter? = null

        fun <T> emit(event: String, input: T) {
            writer?.let {
                scope.launch {
                    val message = gson.toJson(NetworkMessage(event, input))
                    it.println(message)
                    it.flush()
                }
            }
        }
    }
}