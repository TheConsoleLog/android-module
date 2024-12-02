package at.htlwels.bonfire.view.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.htlwels.ires.R

@Composable
fun RegisterField(
    text: MutableState<String>,
    resetError: () -> Unit = {},
    label: String,
    keyboardType: KeyboardType? = null,
    icon: @Composable () -> Unit,
){
    OutlinedTextField(
        keyboardOptions = keyboardType?.let { KeyboardOptions(keyboardType = it) } ?: KeyboardOptions.Default,
        singleLine = true,
        leadingIcon = icon,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        value = text.value,
        onValueChange = {
            text.value = it
            resetError()
            println("jo sicha")
        }
    )
}


@Composable
fun PasswordField(
    password: MutableState<String>,
    resetError: () -> Unit = {}
) {

    var pwVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable { pwVisible = !pwVisible },
                painter = painterResource(
                    if(pwVisible) R.drawable.baseline_visibility_24
                    else R.drawable.baseline_visibility_off_24
                ), contentDescription =  null)
        },
        label = { Text("Passwort") },
        value = password.value,
        onValueChange = { password.value = it; resetError() },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation =
        if(pwVisible) VisualTransformation.None
        else PasswordVisualTransformation() 
    )
}

@Composable
fun ErrorText(text: String){
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = text,
        color = Color.Red
    )
}

@Composable
fun BackButtonRow(
    onClick: () -> Unit,
    rowContent: @Composable () -> Unit = {},
){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            onClick = onClick
        ) { //TO insert to delte
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
        }
        rowContent()
    }
}