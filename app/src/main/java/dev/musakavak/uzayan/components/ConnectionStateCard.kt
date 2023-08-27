package dev.musakavak.uzayan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.socket.ConnectionState

@Composable
fun ConnectionStateCard(
    padding: Dp,
    startService: (String?, Int?, Int?, Boolean?) -> Unit
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
                200 -> ManualPairCard(startService)
                201 -> ConnectingStatus(padding)
                202 -> ConnectedDeviceCard(padding)
            }
        }
    }
}

@Composable
fun ConnectingStatus(padding: Dp) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ConnectionState.connectingStatus?.let {
            CircularProgressIndicator(modifier = Modifier.padding(end = padding))
            Text(text = stringResource(id = it), style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Composable
fun ConnectedDeviceCard(padding: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
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