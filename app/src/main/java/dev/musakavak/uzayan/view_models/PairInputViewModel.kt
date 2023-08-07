package dev.musakavak.uzayan.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PairInputViewModel : ViewModel() {
    private val ipAddressRegex =
        Regex("(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}")

    var isConnectionSecure by mutableStateOf(false)

    //IpAddress
    var ipAddress by mutableStateOf("")
        private set
    var isIpAddressValid by mutableStateOf(true)
        private set

    fun sIpAddress(value: String) {
        ipAddress = value
        isIpAddressValid = value.matches(ipAddressRegex)
        sAllValid()
    }

    //Port
    var port by mutableStateOf<Int?>(null)
        private set
    var isPortValid by mutableStateOf(true)
        private set

    fun sPort(value: String) {
        val v = value.toIntOrNull()
        port = v
        isPortValid = v != null && v <= 65535
        sAllValid()
    }

    //Code
    var code by mutableStateOf<Int?>(null)
        private set
    var isCodeValid by mutableStateOf(true)
        private set

    fun sCode(value: String) {
        code = value.toIntOrNull()
        isCodeValid = value.length == 6
        sAllValid()
    }

    //All
    var isAllValid by mutableStateOf(false)
        private set

    private fun sAllValid() {
        isAllValid = isIpAddressValid && isPortValid && isCodeValid
    }

    private fun getPairParameters() {}
}