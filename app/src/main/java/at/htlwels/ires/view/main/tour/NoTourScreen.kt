package at.htlwels.ires.view.main.tour

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import at.htlwels.ires.R
import at.htlwels.ires.control.NoTourViewModel
import at.htlwels.ires.view.HorizontalSpacer
import at.htlwels.ires.view.VerticalSpacer

@Composable
fun NoTourScreen(
    viewModel: NoTourViewModel,
    tryToFetchAgain: () -> Unit
){

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("No active Tour was found :(")
        TextButton(onClick = tryToFetchAgain) {
            Text("Try again")
        }

        VerticalSpacer(48)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { viewModel.currentScreen.value = at.htlwels.ires.control.NoTourScreen.Joining }
        ) {
            Row {
                Text("Join Tour")
                HorizontalSpacer(8)
                Icon(painter = painterResource(R.drawable.baseline_start_24), null)
            }
        }
        VerticalSpacer(24)
        HorizontalDivider()
        VerticalSpacer(24)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = { viewModel.currentScreen.value = at.htlwels.ires.control.NoTourScreen.Creating }
        ) {
            Text("Create Tour")
            HorizontalSpacer(8)
            Icon(Icons.Default.Add, null)
        }


    }
}