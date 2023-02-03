package dev.musakavak.uzayan.utils

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Bundle
import android.util.Log
import dev.musakavak.uzayan.services.NLService

class MediaSessionManager(private val context: Context) {
    private val tag = "MediaSessionManager"
    fun listen() {

        var c = 0
        val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        manager.addOnActiveSessionsChangedListener({
          it?.forEach {controller ->
              controller.registerCallback(object : MediaController.Callback() {
                  override fun onPlaybackStateChanged(state: PlaybackState?) {
                      super.onPlaybackStateChanged(state)
                     // printBundle(controller.metadata)
                      println("Current: $c")
                      c++
                  }
              })
          }
        }, ComponentName(context, NLService::class.java))
    }

    fun printBundle(bundle:MediaMetadata?){
        val extras = bundle?.keySet()
        Log.w(tag,"-------------------------------------------------")
        extras?.forEach {
            Log.i(tag,"Key: $it ----------- Value: ${bundle.getString(it)}")
        }
        Log.w(tag,"-------------------------------------------------")
    }
}