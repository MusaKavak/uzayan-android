package dev.musakavak.uzayan.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.musakavak.uzayan.AllowListVMFactory
import dev.musakavak.uzayan.AllowListViewModel
import dev.musakavak.uzayan.R
import dev.musakavak.uzayan.managers.AllowListManager
import dev.musakavak.uzayan.models.AllowList

@Composable
fun AllowListColumn(
    allowListManager: AllowListManager,
    vm: AllowListViewModel = viewModel(factory = AllowListVMFactory(allowListManager))
) {
    AllowanceCard(
        R.string.alw_media_sessions,
        vm.allowList.mediaSessions,
        { vm.setAllowance(AllowList::mediaSessions, it) },
    ) {
        Text(text = "efewfokofpOWVGOPWkgvpwokg")
    }
    AllowanceCard(
        R.string.alw_notification_transfer,
        vm.allowList.notificationTransfer,
        { vm.setAllowance(AllowList::notificationTransfer, it) },
    ) {
        println("Recomposed")
        Text(text = "efewfokofpOWVGOPWkgvpwokg")
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
        ) {
            Text(style = MaterialTheme.typography.headlineLarge, text = stringResource(titleId))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
        subSections()
    }
}