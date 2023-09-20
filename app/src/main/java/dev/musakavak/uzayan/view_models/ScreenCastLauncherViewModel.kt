package dev.musakavak.uzayan.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import dev.musakavak.uzayan.models.Screen

class ScreenCastLauncherViewModel : ViewModel() {
    var screen by mutableStateOf<Screen?>(null)

    var width by mutableStateOf("")
    var widthValid by mutableStateOf(true)
    var height by mutableStateOf("")
    var heightValid by mutableStateOf(true)

    var x by mutableStateOf("")
    var xValid by mutableStateOf(true)
    var y by mutableStateOf("")
    var yValid by mutableStateOf(true)

    fun sScreen(screen: Screen) {
        this.screen = screen
        width = screen.width
        height = screen.height
        x = "0"
        y = "0"
    }

    fun sWidth(nWidth: String) {
        nWidth.isDigitsOnly()
        widthValid = isBigger(screen?.width, nWidth)
        width = nWidth
    }

    fun sHeight(nHeight: String) {
        heightValid = isBigger(screen?.height, nHeight)
        height = nHeight
    }

    fun sX(nX: String) {
        xValid = isBiggerThanSumOf(screen?.width, width, nX)
        x = nX
    }

    fun sY(nY: String) {
        yValid = isBiggerThanSumOf(screen?.height, height, nY)
        y = nY
    }


    private fun isBiggerThanSumOf(first: String?, second: String?, third: String?): Boolean {
        return try {
            val f = first!!.toInt()
            val s = second!!.toInt()
            val t = third!!.toInt()
            return (f >= 0 && s >= 0 && t >= 0) && f >= s + t
        } catch (_: Exception) {
            false
        }
    }

    private fun isBigger(first: String?, second: String?): Boolean {
        return try {
            val f = first!!.toInt()
            val s = second!!.toInt()
            return f >= 0 && s >= 0 && f >= s
        } catch (_: Exception) {
            false
        }
    }
}