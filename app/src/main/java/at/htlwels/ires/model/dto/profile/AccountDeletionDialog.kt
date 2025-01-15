package at.htlwels.ires.model.dto.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AccountDeletionDialog(
    closeDeletionDialog: () -> Unit,
    logout: () -> Unit
) {
    //val deletionViewModel = viewModel<AccountDeletionViewModel>()
    //val deletionState by deletionViewModel.accountDeletionState

    val loading = true //deletionState is Resource.Loading

    val context = LocalContext.current
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        titleContentColor = MaterialTheme.colorScheme.onErrorContainer,
        iconContentColor = MaterialTheme.colorScheme.error,
        textContentColor = MaterialTheme.colorScheme.onErrorContainer,
        icon = { Icon(Icons.Default.Warning, null) },
        title = { Text("Account Löschen?") },
        text = { Text("Dies kann nicht rückgängig gemacht werden") },
        onDismissRequest = { if (!loading) closeDeletionDialog() },
        confirmButton = {

            if (!loading) {
                Button(onClick = closeDeletionDialog) {
                    Text("Abbrechen")
                }
            } else {
                CircularProgressIndicator()
            }
        },
        dismissButton = {
            if (!loading) {
                OutlinedButton(
                    border = BorderStroke(
                        ButtonDefaults.outlinedButtonBorder.width,
                        MaterialTheme.colorScheme.error
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    onClick = {
                        //TODO
                    }
                ) {
                    Text("Fortfahren")
                }
            }
        }
    )
}