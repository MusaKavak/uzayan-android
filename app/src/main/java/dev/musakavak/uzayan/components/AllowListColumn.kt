package dev.musakavak.uzayan.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.musakavak.uzayan.AllowListVMFactory
import dev.musakavak.uzayan.AllowListViewModel
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.models.AllowList

@Composable
fun AllowListColumn(
    padding: Dp,
    allowListManager: AllowListManager,
    vm: AllowListViewModel = viewModel(factory = AllowListVMFactory(allowListManager))
) {
    Column(Modifier.fillMaxWidth()) {
        AllowanceCard(
            R.string.alw_notifications,
            vm.allowList.notifications,
            { vm.setAllowance(AllowList::notifications, it) },
        ) {
            SubSection(
                titleId = R.string.alw_notification_controls,
                checked = vm.allowList.notificationControls,
                onCheckedChange = { vm.setAllowance(AllowList::notificationControls, it) },
            )
        }
        Spacer(Modifier.padding(padding))
        AllowanceCard(
            R.string.alw_media_session,
            vm.allowList.mediaSession,
            { vm.setAllowance(AllowList::mediaSession, it) },
        ) {
            SubSection(
                titleId = R.string.alw_media_session_controls,
                checked = vm.allowList.mediaSessionControl,
                onCheckedChange = { vm.setAllowance(AllowList::mediaSessionControl, it) },
            )
        }
        Spacer(Modifier.padding(padding))
        AllowanceCard(
            R.string.alw_file,
            vm.allowList.file,
            { vm.setAllowance(AllowList::file, it) },
        ) {
            SubSection(
                titleId = R.string.alw_send_file,
                checked = vm.allowList.sendFile,
                onCheckedChange = { vm.setAllowance(AllowList::sendFile, it) },
            )
            SubSection(
                titleId = R.string.alw_receive_file,
                checked = vm.allowList.receiveFile,
                onCheckedChange = { vm.setAllowance(AllowList::receiveFile, it) },
            )
            SubSection(
                titleId = R.string.alw_rename_file,
                checked = vm.allowList.renameFile,
                onCheckedChange = { vm.setAllowance(AllowList::renameFile, it) },
            )
            SubSection(
                titleId = R.string.alw_delete_file,
                checked = vm.allowList.deleteFile,
                onCheckedChange = { vm.setAllowance(AllowList::deleteFile, it) },
            )

        }
    }
}

@Composable
private fun AllowanceCard(
    titleId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subSections: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(style = MaterialTheme.typography.headlineLarge, text = stringResource(titleId))
                Switch(checked = checked, onCheckedChange = onCheckedChange)
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (checked) 1f else 0.5f)
            ) {
                subSections()
            }
        }
    }
}

@Composable
private fun SubSection(
    titleId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked, onCheckedChange = onCheckedChange)

        Text(style = MaterialTheme.typography.headlineSmall, text = stringResource(titleId))
    }
}