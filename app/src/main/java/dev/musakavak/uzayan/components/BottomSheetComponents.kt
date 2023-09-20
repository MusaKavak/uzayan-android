package dev.musakavak.uzayan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.socket.ConnectionState
import dev.musakavak.uzayan.socket.Emitter

@Composable

fun RemoteCommandsCard(padding: Dp) {
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier
            .padding(padding)
            .fillMaxWidth()
            .requiredHeight(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            ConnectionState.remoteCommands?.let { commands ->
                commands.forEach {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = it, style = MaterialTheme.typography.headlineSmall)
                        IconButton(
                            onClick = { Emitter.emit("RemoteCommand", it) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = stringResource(
                                    R.string.send_command
                                )
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceTint)
                }
            }
        }
    }
}

