package at.htlwels.ires.view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.htlwels.ires.R
import at.htlwels.ires.view.auth.ErrorText
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VerticalSpacer(size: Int){
    Spacer(modifier = Modifier.height(size.dp))
}

@Composable
fun HorizontalSpacer(size: Int){
    Spacer(modifier = Modifier.width(size.dp))
}

@Composable
fun FullWidthButton(text: String, onClick: () -> Unit){
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProgressIndicatorBox(){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator()
    }
}

@Composable
fun DividerWithText(text: String){

    Box(contentAlignment = Alignment.Center) {
        HorizontalDivider()
        Text(
            color = MaterialTheme.colorScheme.onBackground,
            text = text,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp)
        )
    }
}

@Composable
fun ClickableTextField(
    modifier: Modifier = Modifier,
    showDialog: () -> Unit,
    text: String,
    pointerInput: Any?,
    label: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null
){
    OutlinedTextField(
        value = text,
        onValueChange = { },
        label = { label?.let{ Text(it) } },
        trailingIcon = trailingIcon,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(pointerInput) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showDialog()
                    }
                }
            }
    )
}


@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier,
    selectedDate: MutableState<Long?>
) {
    var showModal by remember { mutableStateOf(false) }

    ClickableTextField(
        showDialog = { showModal = true },
        text = selectedDate.value?.let { convertMillisToDate(it) } ?: "",
        pointerInput = selectedDate.value,
        label = "Date",
        trailingIcon = {
            Icon(Icons.Default.DateRange, null)
        }
    )

    if (showModal) {
        SimpleDatePickerDialog(
            onDateSelected = { selectedDate.value = it },
            onDismiss = { showModal = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}



/** TimePicker TextField and Dialog */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerPickerModal(
    onDone: (TimePickerState) -> Unit
){
    var showDialog by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()

    ClickableTextField(
        showDialog = { showDialog = true },
        text = String.format(Locale.getDefault(),"%02d:%02d", timePickerState.hour, timePickerState.minute),
        pointerInput = timePickerState,
        trailingIcon = { Icon(
            painterResource(R.drawable.baseline_access_time_filled_24), null
        ) },
        label = "Time"
    )

    if (showDialog) {

        AlertDialog(
            onDismissRequest = { showDialog = false },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(onClick = {
                    onDone(timePickerState)
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun ErrorBox(
    retry: () -> Unit,
    errorText: String
){
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorText(errorText)
        VerticalSpacer(8)
        Button(
            onClick = retry
        ) {
            Text("Try Again")
        }
    }
}


@Composable
fun AsyncImageWithFallback(
    model: Any?,
    contentDescription: String? = null,
    modifier:Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
){
    val fallback = painterResource(R.drawable.fallback_image)
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        error = fallback,
        placeholder = fallback,
        fallback = fallback,
        modifier = modifier,
        contentScale = contentScale,
    )
}