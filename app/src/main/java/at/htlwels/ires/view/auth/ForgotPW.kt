package at.htlwels.ires.view.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.htlwels.ires.R
import at.htlwels.ires.control.AuthorizationViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.view.Routes
import at.htlwels.ires.view.VerticalSpacer

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthorizationViewModel,
    setLoginSnackBar: (String, Long) -> Unit,
    navToRoute: (Routes.Authorization) -> Unit
){

    val emailSentState = viewModel.pwEmailSentState

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.signup_background),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )

        Card (
            modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f),
            elevation = CardDefaults.cardElevation(defaultElevation = 64.dp)
        ) {
            Column (modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(40.dp)
            ) {
                BackButtonRow (onClick =  { navToRoute(Routes.Authorization.LoginScreen) })
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if(emailSentState.value is Resource.Success){
                        ResetPwStage2(
                            viewModel = viewModel,
                            navToRoute = navToRoute,
                            setLoginSnackBar = setLoginSnackBar
                        )
                    } else {
                        ResetPwStage1(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResetPwStage1(viewModel: AuthorizationViewModel){
    val usernameState = viewModel.resetPwUsernameState
    val emailSentState = viewModel.pwEmailSentState

    Text(
        "Passwort vergessen",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp
    )
    VerticalSpacer(20)
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Erhalten Sie eine Email um ihr Passwort zurückzusetzen",
        color = MaterialTheme.colorScheme.secondary
    )
    VerticalSpacer(20)

    RegisterField(
        icon = { Icon(Icons.Default.Star, null) },
        text = usernameState,
        label = "Username"
    )


    if(emailSentState.value is Resource.Error){
        val errorMSG = (emailSentState.value as Resource.Error).getMessage()
        VerticalSpacer(8)
        Text("Email konnte nicht gesendet werden: $errorMSG", color = MaterialTheme.colorScheme.error)
    }

    VerticalSpacer(20)

    if(emailSentState.value is Resource.Loading){
        CircularProgressIndicator()
    } else {
        Button(onClick = { viewModel.sendPwEmail(usernameState.value) }) {
            Text("Senden")
        }
    }
}

@Composable
private fun ResetPwStage2(
    viewModel: AuthorizationViewModel,
    navToRoute: (Routes.Authorization) -> Unit,
    setLoginSnackBar: (String, delay: Long) -> Unit
){
    var tokenState by remember { mutableStateOf("")}
    val newPasswordState = remember {mutableStateOf("")}

    val resetPasswordState = viewModel.passwordResetState

    if(resetPasswordState.value is Resource.Success){

        LaunchedEffect(Unit) {
            navToRoute(Routes.Authorization.LoginScreen)
            viewModel.resetPasswordResetStates()
            setLoginSnackBar("Passwort wurde geändert.", 200)
        }
    }

    Text(
        "Email wurde versendet.",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp
    )

    VerticalSpacer(16)

    Text("Geben sie ihren Code hier ein")

    OutlinedTextField(
        label = { Text("Code")},
        value = tokenState,
        onValueChange = {
            if(it.toIntOrNull() != null && it.length <= 6) tokenState = it
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

    VerticalSpacer(16)

    val fm = LocalFocusManager.current
    PasswordField(
        password = newPasswordState,
        onImeAction = { fm.clearFocus() }
    )

    if(resetPasswordState.value is Resource.Error){
        val errorMSG = (resetPasswordState.value as Resource.Error).getMessage()
        VerticalSpacer(8)
        Text("Passwort wurde nicht zurückgesetzt: $errorMSG", color = MaterialTheme.colorScheme.error)
    }

    VerticalSpacer(16)

    if(resetPasswordState.value is Resource.Loading){
        CircularProgressIndicator()
    } else {
        Button(
            onClick = { viewModel.resetPassword(
                resetToken = tokenState,
                newPW = newPasswordState.value
            ) }
        ) {
            Text("Senden")
        }
    }
}