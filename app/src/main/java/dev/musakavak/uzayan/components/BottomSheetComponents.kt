package dev.musakavak.uzayan.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.socket.ConnectionState
import dev.musakavak.uzayan.socket.Emitter

@Composable

fun RemoteCommandsCard(padding: Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
    ) {
        ConnectionState.remoteCommands?.let { commands ->
            commands.forEach {
                ListItem(
                    headlineContent = {
                        Text(text = it)
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { Emitter.emit("RemoteCommand", it) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = stringResource(
                                    R.string.send_command
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}