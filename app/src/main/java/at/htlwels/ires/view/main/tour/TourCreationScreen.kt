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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import at.htlwels.ires.common.Constants.DateUtils.convertMillisToDate
import at.htlwels.ires.control.NoTourViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.model.dto.tour.SimpleTour
import at.htlwels.ires.model.dto.tour.Tour
import at.htlwels.ires.view.VerticalSpacer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourCreationScreen(
    viewModel: NoTourViewModel,
    onSuccess: (Tour) -> Unit,
    updateTopBar: (@Composable () -> Unit) -> Unit,
    navigateBack: () -> Unit
){

    updateTopBar{ TopAppBar(
        title = {},
        navigationIcon = { IconButton(onClick = navigateBack) {
            Icon(Icons.AutoMirrored.Default.KeyboardArrowLeft, null)
        } }
    ) }

    var nameState by remember { mutableStateOf("") }
    var descriptionState by remember { mutableStateOf("") }

    val tourCreationState by viewModel.tourCreationState

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


        val dateRange = remember { mutableStateOf<Pair<Long?, Long?>?>(null) }
        DatePickerFieldToModal(dateRange)

        VerticalSpacer(32)

        if(tourCreationState is Resource.Loading){
            CircularProgressIndicator()
        } else {
            Button(
                enabled = dateRange.value != null,
                onClick = {

                    val startDate = convertMillisToDate(dateRange.value!!.first!!)
                    val endDate = convertMillisToDate(dateRange.value!!.second!!)
                    viewModel.postNewTour(
                        SimpleTour(nameState, descriptionState, startDate, endDate),
                        onSuccess
                    )
                }
            ) { Text("Tour erstellen") }
        }

        tourCreationState.let {
            if(it is Resource.Error) ErrorText( it.getMessage())
            println("error detected")
        }
    }
}


@Composable
fun DatePickerFieldToModal(
    selectedDate: MutableState<Pair<Long?, Long?>?>,
    modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate.value
            ?.let {
                convertMillisToDate(
                    millis = it.first!!,
                    format = "MM/DD/YYYY"
                ) + "  -  " + convertMillisToDate(
                    millis = it.second!!,
                    format = "MM/DD/YYYY"
                )
            }
            ?: "",
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
            onDateRangeSelected = { selectedDate.value = it },
            dismiss = { showModal = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    dismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = dismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    ))
                    dismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = dismiss) { Text("Cancel") }
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


