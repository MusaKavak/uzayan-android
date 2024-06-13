package dev.musakavak.uzayan.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.socket.ConnectionState

@Composable
fun ConnectionStateCard(
    padding: Dp,
    startService: (String?, Int?, Int?, Boolean?) -> Unit,
    closeConnection: () -> Unit,
    startServiceFromUri: (Uri) -> Unit,
    setSheetContent: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
        ) {
            when (ConnectionState.currentStatus) {
                200 -> ManualPairCard(startService, startServiceFromUri)
                201 -> ConnectingStatus(padding,closeConnection)
                202 -> ConnectedDeviceCard(padding, setSheetContent,closeConnection)
            }
        }
    }
}

@Composable
fun ConnectingStatus(padding: Dp,closeConnection:()->Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ConnectionState.connectingStatus?.let {
            CircularProgressIndicator(modifier = Modifier.padding(end = padding))
            Text(text = stringResource(id = it), style = MaterialTheme.typography.headlineLarge)
            OutlinedButton(onClick = closeConnection) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    }
}

@Composable
fun ConnectedDeviceCard(padding: Dp, setSheetContent: (String) -> Unit,closeConnection: () -> Unit) {
    Column(modifier = Modifier.fillMaxHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(padding)
                ConnectedClientName()
            }
            ConnectionState.isConnectionSecure?.let {
                val painter =
                    painterResource(if (it) R.drawable.lock else R.drawable.lock_open)
                val description =
                    stringResource(if (it) R.string.connection_secure else R.string.connection_not_secure)
                val tint =
                    if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                Icon(
                    painter = painter,
                    contentDescription = description,
                    tint = tint,
                )
            }
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                AssistChip(
                    onClick = { setSheetContent("commands") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.terminal),
                            contentDescription = stringResource(R.string.remote_commands)
                        )
                    },
                    label = {
                        Text(text = stringResource(R.string.remote_commands))
                    }
                )
            }
            item {
                AssistChip(
                    onClick = { setSheetContent("screencast") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.terminal),
                            contentDescription = stringResource(R.string.screencast)
                        )
                    },
                    label = {
                        Text(text = stringResource(R.string.screencast))
                    }
                )
            }
            item {
                AssistChip(
                    onClick = closeConnection,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dangerous Action",
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(R.string.close),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                )
            }
        }
    }
}

@Composable
fun ConnectedClientName() {
    Column {
        Text(
            text = stringResource(id = R.string.connected_to),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text =
            if (ConnectionState.connectedClientName.isNullOrBlank())
                stringResource(id = R.string.unknown)
            else ConnectionState.connectedClientName!!
        )
    }
}

@Composable
fun AppIcon(padding: Dp) {
    Icon(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = padding),
        painter = painterResource(R.drawable.uzayan_fit),
        contentDescription = stringResource(R.string.app_icon_description),
        tint = MaterialTheme.colorScheme.primary
    )
}