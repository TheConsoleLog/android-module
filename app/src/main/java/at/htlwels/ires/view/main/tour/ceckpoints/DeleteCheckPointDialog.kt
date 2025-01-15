package at.htlwels.ires.view.main.tour.ceckpoints

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import at.htlwels.ires.control.DeleteCheckPointViewModel

@Composable
fun DeleteCheckPointDialog(
    dismiss: () -> Unit,
    checkPointID: Int,
    reloadTour: () -> Unit
){
    val viewModel = viewModel<DeleteCheckPointViewModel>()
    val context = LocalContext.current

    AlertDialog(
        title = { Text("Delete Checkpoint?") },
        onDismissRequest = dismiss,
        confirmButton = {
            Button(
                onClick = {
                    viewModel.delete(
                        checkPointID,
                        onResult = {

                            Toast.makeText(context, it ?: "Checkpoint wurde gelöscht", Toast.LENGTH_SHORT).show()

                            it ?: run {
                                dismiss()
                                reloadTour()
                            }
                        }
                    )
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = dismiss) {
                Text("Cancel")
            }
        }
    )
}