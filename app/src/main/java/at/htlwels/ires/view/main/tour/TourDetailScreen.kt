package at.htlwels.ires.view.main.tour

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.htlwels.ires.R
import at.htlwels.ires.common.Constants
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.view.HorizontalSpacer
import at.htlwels.ires.view.Routes
import at.htlwels.ires.view.VerticalSpacer
import at.htlwels.ires.view.main.tour.ceckpoints.DeleteCheckPointDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TourDetailScreen(
    updateTopBar: (@Composable () -> Unit) -> Unit,
    tour: Tour,
    reloadTourState: () -> Unit,
    navTo: (Routes) -> Unit
){

    var showLeaveTourDialog by remember { mutableStateOf(false) }
    var showAccessCodeDialog by remember { mutableStateOf(false) }
    var checkPointToDelete by remember { mutableStateOf<Int?>(null) }

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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
            ) {
                Text("Next Program Items", style = MaterialTheme.typography.headlineSmall)

                if(tour.isTourGuide){
                    IconButton(
                        onClick = { navTo(Routes.Main.TourScreen.CreateCheckPoint(tour.tId)) }
                    ) {
                        Icon(Icons.Default.Add, null)
                    }
                }
            }
        }

        this.items(tour.checkpoints){

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Column (modifier = Modifier.padding(16.dp).weight(1f)) {
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

                        VerticalSpacer(8)

                        val dateTime = LocalDateTime.parse(it.time, Constants.DateUtils.isoFormatter)

                        Text(Constants.DateUtils.convertToDateString(dateTime))

                    }
                    IconButton(
                        onClick = { checkPointToDelete = it.cId }
                    ) {
                        Icon(Icons.Default.Delete, null)
                    }
                }
            }

            VerticalSpacer(8)
        }
    }


    if(showAccessCodeDialog){
        AccessCodeDialog(
            dismiss = { showAccessCodeDialog = false },
            accessCode = tour.accessCode
        )
    }

    if(showLeaveTourDialog){
        LeaveTourDialog(
            dismiss = { showLeaveTourDialog = false },
            tourID = tour.tId,
            reloadTourState = reloadTourState
        )
    }

    checkPointToDelete?.let {
        DeleteCheckPointDialog(
            dismiss = { checkPointToDelete = null },
            checkPointID = it,
            reloadTour = reloadTourState
        )
    }
}
