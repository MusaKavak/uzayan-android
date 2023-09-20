package dev.musakavak.uzayan.components

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.ScreenCastActivity
import dev.musakavak.uzayan.models.Screen
import dev.musakavak.uzayan.socket.ConnectionState
import dev.musakavak.uzayan.socket.Emitter
import dev.musakavak.uzayan.view_models.ScreenCastLauncherViewModel

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun ScreenCastLauncher(
    padding: Dp,
    vm: ScreenCastLauncherViewModel = viewModel(ScreenCastLauncherViewModel::class.java)
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val kybopt = KeyboardOptions(
        keyboardType = KeyboardType.Number
    )
    Card(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
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
            ScreenSelection(padding, vm.screen, vm::sScreen)
            Spacer(Modifier.height(padding))
            SizeInputs(
                padding,
                vm.width,
                vm::sWidth,
                vm.widthValid,
                vm.height,
                vm::sHeight,
                vm.heightValid,
                kybopt
            )
            Spacer(Modifier.height(padding))
            LocationInputs(
                padding,
                vm.x,
                vm::sX,
                vm.xValid,
                vm.y,
                vm::sY,
                vm.yValid,
                kybopt
            )

            Button(onClick = {
                val intent = Intent(
                    context,
                    ScreenCastActivity::class.java
                ).apply {
                    action = "mainToScreencast"
                    putExtra("screen_name", vm.screen?.name)
                    putExtra("width", vm.width)
                    putExtra("height", vm.height)
                    putExtra("x", vm.x)
                    putExtra("y", vm.y)
                }

                context.startActivity(intent)
            }) {
                Text(text = "Play")
            }
        }
    }
}

@Composable
fun SizeInputs(
    padding: Dp,
    width: String,
    setWidth: (String) -> Unit,
    widthValid: Boolean,
    height: String,
    setHeight: (String) -> Unit,
    heightValid: Boolean,
    kybopt: KeyboardOptions
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = width,
            onValueChange = setWidth,
            label = { Text(stringResource(R.string.width)) },
            isError = !widthValid,
            keyboardOptions = kybopt
        )
        Spacer(Modifier.width(padding))
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = height,
            onValueChange = setHeight,
            label = { Text(stringResource(R.string.height)) },
            isError = !heightValid,
            keyboardOptions = kybopt
        )
    }
}

@Composable
fun LocationInputs(
    padding: Dp,
    x: String,
    sX: (String) -> Unit,
    xValid: Boolean,
    y: String,
    sY: (String) -> Unit,
    yValid: Boolean,
    kybopt: KeyboardOptions
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = x,
            onValueChange = sX,
            label = { Text("X") },
            isError = !xValid,
            keyboardOptions = kybopt
        )
        Spacer(Modifier.width(padding))
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = y,
            onValueChange = sY,
            label = { Text("Y") },
            isError = !yValid,
            keyboardOptions = kybopt
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSelection(
    padding: Dp,
    screen: Screen?,
    setScreen: (Screen) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(1f),
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = screen?.nameWithSizes ?: "",
                onValueChange = {},
                label = { Text(stringResource(R.string.screen)) },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                ConnectionState.screens?.forEach {
                    DropdownMenuItem(
                        text = { Text(it.nameWithSizes) },
                        onClick = { setScreen(it); expanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(padding))
        IconButton(onClick = { Emitter.emit("ScreenInfo", null) }) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = stringResource(R.string.refresh),
            )
        }
    }
}