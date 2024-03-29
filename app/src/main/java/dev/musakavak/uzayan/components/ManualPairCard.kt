package dev.musakavak.uzayan.components

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.tools.checkCameraPermission
import dev.musakavak.uzayan.view_models.PairInputViewModel

@Composable
fun ManualPairCard(
    startService: (String?, Int?, Int?, Boolean?) -> Unit,
    startServiceFromUri: (Uri) -> Unit,
    vm: PairInputViewModel = viewModel(PairInputViewModel::class.java)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SecureConnection(vm.isConnectionSecure) { vm.isConnectionSecure = it }
        Spacer(modifier = Modifier.height(16.dp))
        IPAddressInput(vm.ipAddress, vm.isIpAddressValid) { vm.sIpAddress(it) }
        Spacer(modifier = Modifier.height(16.dp))
        PortAndCode(
            vm.port,
            { vm.sPort(it) },
            vm.isPortValid,
            vm.code,
            { vm.sCode(it) },
            vm.isCodeValid
        )
        Spacer(modifier = Modifier.height(16.dp))
        Buttons(
            { startService(vm.ipAddress, vm.port, vm.code, vm.isConnectionSecure) },
            startServiceFromUri,
            vm.isAllValid
        )
    }
}

@Composable
fun SecureConnection(
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.secure_connection),
            style = MaterialTheme.typography.headlineLarge
        )
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
fun IPAddressInput(
    value: String,
    isValid: Boolean,
    onChange: (String) -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onChange,
            isError = !isValid,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            textStyle = MaterialTheme.typography.headlineSmall,
            label = {
                Text(text = "Ip Address")
            }
        )
    }
}

@Composable
fun PortAndCode(
    portValue: Int?,
    onPortChange: (String) -> Unit,
    isPortValid: Boolean,
    codeValue: Int?,
    onCodeChange: (String) -> Unit,
    isCodeValid: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        val kybopt = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
        val mdf = Modifier.weight(1f)
        OutlinedTextField(
            modifier = mdf,
            value = "${portValue ?: ""}",
            onValueChange = onPortChange,
            isError = !isPortValid,
            keyboardOptions = kybopt,
            textStyle = MaterialTheme.typography.headlineSmall,
            label = {
                Text(text = "Port")
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedTextField(
            modifier = mdf,
            value = "${codeValue ?: ""}",
            onValueChange = onCodeChange,
            isError = !isCodeValid,
            keyboardOptions = kybopt,
            textStyle = MaterialTheme.typography.headlineSmall,
            label = {
                Text(text = "Code")
            }
        )
    }
}

@Composable
fun Buttons(
    startService: () -> Unit,
    startServiceFromUri: (Uri) -> Unit,
    isAllValid: Boolean
) {
    val context = LocalContext.current

    val qrCodeLauncher = rememberLauncherForActivityResult(ScanContract()) {
        if (it.contents.isNullOrBlank()) {
            Toast.makeText(context, R.string.invalid_qr_code, Toast.LENGTH_LONG).show()
        } else {
            val uri = Uri.parse(it.contents)
            if (uri.host == "uzayan-pair")
                startServiceFromUri(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            openCameraToScan(qrCodeLauncher)
        } else {
            Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onClick = {
                if (checkCameraPermission(context)) {
                    openCameraToScan(qrCodeLauncher)
                } else requestCameraPermission(cameraPermissionLauncher)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.scan),
                contentDescription = stringResource(R.string.scan_qr_code),
                modifier = Modifier.size(24.dp, 24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.scan),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {
                if (isAllValid) startService()
            }, enabled = isAllValid,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.connect),
                style = MaterialTheme.typography.headlineSmall

            )
        }
    }
}

fun openCameraToScan(launcher: ManagedActivityResultLauncher<ScanOptions, ScanIntentResult>) {
    val options = ScanOptions()
    options.setCameraId(0)
    options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
    options.setTorchEnabled(false)
    options.setBeepEnabled(false)
    options.setOrientationLocked(false)

    launcher.launch(options)
}

fun requestCameraPermission(launcher: ManagedActivityResultLauncher<String, Boolean>) {
    launcher.launch(Manifest.permission.CAMERA)
}