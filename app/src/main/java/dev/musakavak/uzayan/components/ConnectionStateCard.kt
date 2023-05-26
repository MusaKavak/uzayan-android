package dev.musakavak.uzayan.components

import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.musakavak.uzayan.socket.TcpSocket

@Composable
@Preview
fun ConnectionStateCard() {
    Card(
        modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        if (TcpSocket.connectedClientPort != null) {
            Text(text = "Connected To Port ${TcpSocket.connectedClientPort}")
        } else {
            Text(text = "No Client Connected")
        }
    }
}