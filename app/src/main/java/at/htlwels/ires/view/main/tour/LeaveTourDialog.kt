package at.htlwels.ires.view.main.tour

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import at.htlwels.ires.R
import at.htlwels.ires.control.LeaveTourViewModel

@Composable
fun LeaveTourDialog(
    dismiss: () -> Unit,
    tourID: Int,
    reloadTourState: () -> Unit
){
    val tourExitVM = viewModel<LeaveTourViewModel>()
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            Button(
                onClick = {
                    println("attempting to leave tour")
                    tourExitVM.leaveTour(
                        tourID,
                        onResult = { errorMessage ->
                            errorMessage
                                ?.let { Toast.makeText(context, "Failed to leave tour: $errorMessage", Toast.LENGTH_LONG).show() }
                                ?: run {
                                    Toast.makeText(context, "Left tour", Toast.LENGTH_LONG).show()
                                    reloadTourState()
                                }
                        }
                    )
                }
            ) {
                Text("Leave")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = dismiss
            ) {
                Text("Cancel")
            }
        },
        icon = { Icon(painterResource(R.drawable.baseline_logout_24), null) },
        title = { Text("Leave tour?") }
    )
}