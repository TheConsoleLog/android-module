package at.htlwels.ires.view.main.tour

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import at.htlwels.bonfire.view.auth.ErrorText
import at.htlwels.ires.control.TourViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.view.VerticalSpacer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TourCreationScreen(
    viewModel: TourViewModel
){
    var nameState by remember { mutableStateOf("") }
    var descriptionState by remember { mutableStateOf("") }

    val tourCreationState by viewModel.tourCreationState

    if(tourCreationState is Resource.Success){
        viewModel.startTour()
    }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Create Tour", style = MaterialTheme.typography.headlineMedium)
        VerticalSpacer(32)

        OutlinedTextField(
            value = nameState,
            onValueChange = { nameState = it },
            label = { Text("Name") }
        )
        VerticalSpacer(8)

        OutlinedTextField(
            value = descriptionState,
            onValueChange = { descriptionState = it },
            minLines = 3,
            label = { Text("Short Description") }
        )
        VerticalSpacer(16)


        DatePickerFieldToModal()

        VerticalSpacer(32)

        if(tourCreationState is Resource.Loading){
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    viewModel.postNewTour(SimpleTour(nameState, descriptionState, "02.05.2006", "02.05.2006"))
                }
            ) { Text("Tour erstellen") }
        }

        tourCreationState.let {
            if(it is Resource.Error) ErrorText(it.message)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DateRangePicker(
            headline = {
                Box (
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DateRangePickerDefaults. DateRangePickerHeadline(
                        selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                        selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                        displayMode = dateRangePickerState.displayMode,
                        dateFormatter = DatePickerDefaults.dateFormatter()
                    )
                }
            },

            state = dateRangePickerState,
            title = {  },
            showModeToggle = false,
            modifier = Modifier.fillMaxWidth().height(500.dp)
        )
    }
}

@Composable
fun DatePickerFieldToModal(modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Pair<Long?, Long?>?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it.first!!) } ?: "",
        onValueChange = { },
        label = { Text("Choose Date Range") },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, null)
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) showModal = true

                }
            }
    )

    if (showModal) {
        DateRangePickerModal(
            onDateRangeSelected = { selectedDate = it },
            onDismiss = { showModal = false }
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}