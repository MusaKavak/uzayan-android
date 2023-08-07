package dev.musakavak.uzayan.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.socket.ConnectionState

@Composable
fun ConnectionStateCard(padding: Dp) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (ConnectionState.connectedClientName == null) {
                ManualPairCard(padding)
            }
        }
    }
}

@Composable
fun ConnectionStateCardHeader(padding: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AppIcon()
        ClientName()
    }
}

@Composable
fun ClientName() {
    var text = stringResource(R.string.no_connection)
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun AppIcon() {
    val appIcon = ImageVector.vectorResource(R.drawable.uzayan_icon)
    Image(
        imageVector = appIcon,
        contentDescription = stringResource(R.string.app_icon_description),
        contentScale = ContentScale.Fit,
    )
}