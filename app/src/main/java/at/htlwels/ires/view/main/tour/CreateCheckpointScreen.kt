package at.htlwels.ires.view.main.tour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.htlwels.ires.control.CreateCheckpointViewModel
import at.htlwels.ires.view.HorizontalSpacer
import at.htlwels.ires.view.TimerPickerModal
import at.htlwels.ires.view.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCheckpointScreen(
    tourID: Int,
    updateTopBar: (@Composable () -> Unit) -> Unit,
    viewModel: CreateCheckpointViewModel,
    navBack: () -> Unit
){
    updateTopBar{
        TopAppBar(
            title = { Text("Create Tour Item") },
            navigationIcon = {
                IconButton(
                    onClick = navBack
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var postalCode by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("") }
        var street by remember { mutableStateOf("") }
        var houseNumber by remember { mutableStateOf("") }
        var isMeetingPoint by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Checkpoint Name") },
        )
        VerticalSpacer(8)

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )
        VerticalSpacer(8)

        Row (modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.65f),
                value = street,
                onValueChange = { street = it },
                label = { Text("Street") }
            )

            HorizontalSpacer(8)

            OutlinedTextField(
                value = houseNumber,
                onValueChange = { houseNumber = it },
                label = { Text("Number") }
            )
        }

        Row (modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(0.65f),
                value = city,
                onValueChange = { city = it },
                label = { Text("City") }
            )

            HorizontalSpacer(8)

            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Postcode") }
            )
        }

        VerticalSpacer(8)

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") }
        )


        val date = remember { mutableStateOf<Long?>(null) }

        at.htlwels.ires.view.DatePickerFieldToModal(selectedDate = date)

        TimerPickerModal() //TODO TODO TODO

    }
}

