package dev.musakavak.uzayan.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.models.AllowList
import dev.musakavak.uzayan.tools.checkNotificationAccessPermission
import dev.musakavak.uzayan.tools.checkStorageAccessPermission
import dev.musakavak.uzayan.view_models.AllowListVMFactory
import dev.musakavak.uzayan.view_models.AllowListViewModel

@Composable
fun AllowListColumn(
    padding: Dp,
    allowListManager: AllowListManager,
    vm: AllowListViewModel = viewModel(factory = AllowListVMFactory(allowListManager))
) {
    val context = LocalContext.current

    val notificationIcon = painterResource(R.drawable.notifications)
    val folderIcon = painterResource(R.drawable.folder)

    PermissionAlertDialog(
        vm.isDialogVisible,
        vm::dismissDialog,
        vm.onConfirm,
        vm.titleId,
        vm.textId,
        vm.icon
    )

    var toggleOnResume by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

                if (checkNotificationAccessPermission(context)) {
                    if (toggleOnResume == "notifications") {
                        vm.setAllowance(AllowList::notifications, true)
                    }
                    if (toggleOnResume == "mediaSession") {
                        vm.setAllowance(AllowList::mediaSession, true)
                    }
                } else {
                    vm.setAllowance(AllowList::notifications, false)
                    vm.setAllowance(AllowList::mediaSession, false)
                }

                if (checkStorageAccessPermission(context)) {
                    if (toggleOnResume == "file") {
                        vm.setAllowance(AllowList::file, true)
                    }
                } else {
                    vm.setAllowance(AllowList::file, false)
                }

                toggleOnResume = ""
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    Column(Modifier.fillMaxWidth()) {
        AllowanceCard(
            R.string.alw_notifications,
            vm.allowList.notifications,
            {
                if (it && !checkNotificationAccessPermission(context)) {
                    vm.onConfirm = {
                        toggleOnResume = "notifications"
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                    vm.titleId = R.string.permission_notification_title
                    vm.textId = R.string.permission_notification_text
                    vm.icon = notificationIcon
                    vm.isDialogVisible = true
                } else {
                    vm.setAllowance(AllowList::notifications, it)
                }
            },
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
            {
                if (it && !checkNotificationAccessPermission(context)) {
                    vm.onConfirm = {
                        toggleOnResume = "mediaSession"
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                    vm.titleId = R.string.permission_notification_title
                    vm.textId = R.string.permission_notification_text
                    vm.icon = notificationIcon
                    vm.isDialogVisible = true
                } else {
                    vm.setAllowance(AllowList::mediaSession, it)
                }
            },
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
            {
                if (it && !checkStorageAccessPermission(context)) {
                    vm.onConfirm = {
                        toggleOnResume = "file"
                        requestStoragePermission(context, launcher)
                    }
                    vm.titleId = R.string.permission_storage_title
                    vm.textId = R.string.permission_storage_text
                    vm.icon = folderIcon
                    vm.isDialogVisible = true
                } else {
                    vm.setAllowance(AllowList::file, it)
                }
            },
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
    subSections: @Composable (ColumnScope.() -> Unit)
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

@Composable
private fun PermissionAlertDialog(
    isDialogVisible: Boolean,
    dismissDialog: () -> Unit,
    onConfirm: () -> Unit,
    titleId: Int,
    textId: Int,
    painter: Painter?
) {
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = dismissDialog,
            confirmButton = {
                Button(onClick = { onConfirm(); dismissDialog() }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                OutlinedButton(onClick = dismissDialog) { Text(stringResource(R.string.cancel)) }
            },
            title = { Text(stringResource(titleId)) },
            text = { Text(stringResource(textId)) },
            icon = {
                painter?.let {
                    Icon(
                        painter = painter, contentDescription = null
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                decorFitsSystemWindows = true
            )
        )
    }
}

fun requestStoragePermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    } else {
        Toast.makeText(context, R.string.unsupported, Toast.LENGTH_LONG).show()
    }
}