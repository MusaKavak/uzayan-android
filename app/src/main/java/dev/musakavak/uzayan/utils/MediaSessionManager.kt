package dev.musakavak.uzayan.utils

import android.content.ComponentName
import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import dev.musakavak.uzayan.services.NLService

class MediaSessionManager(context: Context) {
    private val manager: MediaSessionManager =
        context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val componentName = ComponentName(context, NLService::class.java)
    private var counter = 0

    private data class PreviousController(
        val controller: MediaController,
        val callback: MediaController.Callback
    )

    private var previousControllers: MutableList<PreviousController> = mutableListOf()

    fun listen() {
        listenControllers(manager.getActiveSessions(componentName))
        manager.addOnActiveSessionsChangedListener({
            listenControllers(it)
        }, componentName)
    }

    private fun listenControllers(controllers: List<MediaController>?) {
        previousControllers.forEach {
            it.controller.unregisterCallback(it.callback)
        }
        previousControllers.clear()
        controllers?.let {
            println("ListSize:       " + it.size)
            it.forEach { controller ->
                val callback = createCallback(controller)
                println("Token: " + controller.sessionToken)
                previousControllers.add(PreviousController(controller,callback))
                controller.registerCallback(callback)
            }
        }
    }

    private fun createCallback(controller: MediaController): MediaController.Callback {
        return object : MediaController.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackState?) {
                super.onPlaybackStateChanged(state)
                println(counter)
                counter++
            }
        }
    }
}

