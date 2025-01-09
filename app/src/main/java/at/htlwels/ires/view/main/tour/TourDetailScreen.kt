package at.htlwels.ires.view.main.tour

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

    LazyColumn (modifier = Modifier.padding(horizontal = 16.dp)) {
        this.item {
            Column(modifier = Modifier.fillMaxWidth()) {

                Card(modifier = Modifier.fillMaxWidth()) {
                    Row (modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Default.Info, null)
                        HorizontalSpacer(8)
                        Text(tour.description)
                    }
                }

                VerticalSpacer(16)

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {}
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically){
                            Icon(painterResource(R.drawable.baseline_people_24), null)
                            HorizontalSpacer(8)
                            Text("${tour.participants.size} Participants")
                        }
                    }

                    OutlinedButton(
                        onClick = { showAccessCodeDialog = true }
                    ) {
                        Text("Invite more people")
                    }
                }

                VerticalSpacer(16)

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

                VerticalSpacer(32)
            }
        }

        this.stickyHeader {
            Text("Next Program Items", style = MaterialTheme.typography.headlineSmall)
            VerticalSpacer(16)
        }

        this.items(tour.checkpoints){

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column (modifier = Modifier.padding(16.dp)) {
                    Text(it.name, fontWeight = FontWeight.Bold)
                    Text(it.description)
                    VerticalSpacer(4)

                    val context = LocalContext.current
                    OutlinedButton(
                        onClick = {
                            val encodedLocation = it.location.run {
                                Uri.encode("$street $houseNumber, $postCode $city")
                            }
                            val gmmIntentUri = Uri.parse("geo:0,0?q=$encodedLocation")

                            Intent(Intent.ACTION_VIEW, gmmIntentUri).let {
                                it.setPackage("com.google.android.apps.maps")
                                context.startActivity(it)
                            }
                        }
                    ) {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null)
                            VerticalSpacer(8)
                            Text("#${it.location.houseNumber} ${it.location.street}")
                        }
                    }
                }
            }
            VerticalSpacer(8)
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
                VerticalSpacer(16)
                QrCodeView(
                    data = tour.accessCode,
                    modifier = Modifier
                        .size(250.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
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
            onDismissRequest = { showLeaveTourDialog = false },
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
            title = { Text("Leave tour?") }
        )
    }
}