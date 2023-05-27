package dev.musakavak.uzayan.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.tools.PairTool

@Composable
fun ConnectionStateCard(urlArgs: Uri?, deviceName: String) {
    urlArgs?.let {
        val ip = it.getQueryParameter("ip")
        val port = it.getQueryParameter("port")?.toIntOrNull()
        val code = it.getQueryParameter("code")
        val name = it.getQueryParameter("name")
        if (ip != null && port != null && code != null) {
            PairTool().sendPairRequest(ip, port, code, deviceName)
            Emitter.connectedClientName = name
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon()
            ClientName()
        }
    }
}

@Composable
fun ClientName() {
    var text = stringResource(R.string.no_connection)
    Emitter.connectedClientName?.let {
        text = it
    }
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun AppIcon() {
    val appIcon = ImageVector.vectorResource(R.drawable.uzayan_icon)
    Image(
        imageVector = appIcon,
        contentDescription = stringResource(R.string.app_icon_description),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(
            colorResource(
                if (Emitter.connectedClientName == null) R.color.icon_tint_disconnected
                else R.color.icon_tint_connected
            )
        )
    )

}