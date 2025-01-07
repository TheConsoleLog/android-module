package at.htlwels.ires.view.main.tour

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import at.htlwels.ires.R
import at.htlwels.ires.common.Constants
import at.htlwels.ires.control.LeaveTourViewModel
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.view.HorizontalSpacer
import at.htlwels.ires.view.VerticalSpacer
import com.lightspark.composeqr.QrCodeView
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourDetailScreen(
    updateTopBar: (@Composable () -> Unit) -> Unit,
    tour: Tour,
    reloadTourState: () -> Unit
){

    var showLeaveTourDialog by remember { mutableStateOf(false) }
    var showAccessCodeDialog by remember { mutableStateOf(false) }

    updateTopBar{
        TopAppBar(
            title = { Text(tour.name) },
            actions = { IconButton(
                onClick = { showLeaveTourDialog = true }
            ){
                Icon(painterResource(R.drawable.baseline_logout_24), null)
            }}
        )
    }



    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedButton(
            onClick = { showAccessCodeDialog = true }
        ) {
            Text("Invite more people")
        }

        val start = LocalDate.parse(tour.startDate, Constants.DateUtils.isoFormatter)
        val end = LocalDate.parse(tour.endDate, Constants.DateUtils.isoFormatter)
        val now = LocalDate.now()

        if(start.isAfter(now)){
            val daysUntilStart = ChronoUnit.DAYS.between(now, start)
            Text("Starts in $daysUntilStart days")
        } else {
            val daysUntilEnd = ChronoUnit.DAYS.between(now, end)
            Text("Ends in $daysUntilEnd days")
        }

        Text(tour.description)

        TextButton(
            onClick = {}
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(painterResource(R.drawable.baseline_people_24), null)
                HorizontalSpacer(8)
                //Text("${tour.participants.size} Participants")
            }
        }
    }

    if(showAccessCodeDialog){

        Dialog(
            onDismissRequest = { showAccessCodeDialog = false }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_person_add_24),
                    null,
                    modifier = Modifier.size(40.dp)
                )
                VerticalSpacer(16)
                Text("Invite people", style = MaterialTheme.typography.headlineMedium)
                VerticalSpacer(8)
                Text("Use this code to add people to the group!")
                VerticalSpacer(8)
                QrCodeView(
                    data = tour.accessCode,
                    modifier = Modifier.size(250.dp)
                )
                VerticalSpacer(16)
                Text("Access Code: ${tour.accessCode}")
            }
        }
    }

    if(showLeaveTourDialog){

        val tourExitVM = viewModel<LeaveTourViewModel>()
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(
                    onClick = {
                        println("attempting to leave tour")
                        tourExitVM.leaveTour(
                            tour.tId,
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
                    onClick = { showLeaveTourDialog = false }
                ) {
                    Text("Cancel")
                }
            },
            icon = { Icon(painterResource(R.drawable.baseline_logout_24), null)},
            title = { Text("Leave tour?")}
        )
    }
}