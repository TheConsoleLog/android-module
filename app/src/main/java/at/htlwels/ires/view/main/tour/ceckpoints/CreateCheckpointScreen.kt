package at.htlwels.ires.view.main.tour.ceckpoints

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import at.htlwels.ires.common.Constants
import at.htlwels.ires.control.CreateCheckpointViewModel
import at.htlwels.ires.model.dto.tour.Checkpoint
import at.htlwels.ires.model.dto.tour.Location
import at.htlwels.ires.view.DatePickerFieldToModal
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

        val focusManager = LocalFocusManager.current

        TextFieldWithFocusDirection(
            value = name,
            onValueChange = { name = it },
            label = "Item Name",
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        VerticalSpacer(8)

        TextFieldWithFocusDirection(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Row (modifier = Modifier.fillMaxWidth()) {
            TextFieldWithFocusDirection(
                modifier = Modifier.fillMaxWidth(0.65f),
                value = street,
                onValueChange = { street = it },
                label = "Street",
                onImeAction = { focusManager.moveFocus(FocusDirection.Right) }
            )

            HorizontalSpacer(8)

            TextFieldWithFocusDirection(
                value = houseNumber,
                onValueChange = { houseNumber = it },
                label = "Number",
                keyboardType = KeyboardType.Number,
                onImeAction = {
                    focusManager.moveFocus(FocusDirection.Down)
                    focusManager.moveFocus(FocusDirection.Left)
                }
            )
        }

        Row (modifier = Modifier.fillMaxWidth()) {
            TextFieldWithFocusDirection(
                modifier = Modifier.fillMaxWidth(0.65f),
                value = city,
                onValueChange = { city = it },
                label = "City",
                onImeAction = { focusManager.moveFocus(FocusDirection.Right) }
            )

            HorizontalSpacer(8)

            TextFieldWithFocusDirection(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = "Postcode",
                keyboardType = KeyboardType.Number,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )
        }

        VerticalSpacer(8)

        TextFieldWithFocusDirection(
            value = country,
            onValueChange = { country = it },
            label = "Country",
            onImeAction = { focusManager.clearFocus() }
        )


        val date = remember { mutableStateOf<Long?>(null) }
        DatePickerFieldToModal(selectedDate = date)

        //time of TimePickerState is 0:00 by default
        var time by remember { mutableStateOf(Time(0,0)) }
        TimerPickerModal(
            onDone = { time = Time(it.hour, it.minute) }
        )
        VerticalSpacer(16)

        val context = LocalContext.current

        Box(modifier = Modifier.align(Alignment.End)){
            Button(
                onClick = {

                    var error: String? = null

                    if(postalCode.toIntOrNull() == null){
                        error = "PostCode is not a number"
                    } else if(date.value == null){
                        error = "Date is not valid"
                    }

                    error
                        ?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                        ?: run {

                            viewModel.post(
                                Checkpoint(
                                    description = description,
                                    isMeetingTime = isMeetingPoint,
                                    location = Location(
                                        city = city,
                                        country = country,
                                        houseNumber = houseNumber,
                                        postCode = postalCode.toInt(),
                                        street = street,
                                        latitude = 0,
                                        longtitude = 0 //TODO?
                                    ),
                                    name = name,
                                    time = Constants.DateUtils.convertToDateTime(
                                        hour = time.hour,
                                        minute = time.minute,
                                        dateInMillis = date.value!!
                                    ),
                                    tourId = tourID
                                ),
                                onResult = { errorMessage ->
                                    Toast.makeText(
                                        context,
                                        errorMessage ?: "Successfully created Tour Item",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    errorMessage ?: run { navBack() }
                                }
                            )
                    }
                }
            ) {
                Text("Confirm")
            }
        }
    }
}

data class Time(
    val hour: Int,
    val minute: Int
)

@Composable
fun TextFieldWithFocusDirection(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType? = null,
    onImeAction: KeyboardActionScope.() -> Unit = {},
    label: String? = null
){

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { label?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(
            keyboardType =  keyboardType ?: KeyboardType.Unspecified,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = onImeAction)
    )
}