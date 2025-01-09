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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.htlwels.ires.view.FullWidthButton
import at.htlwels.ires.R
import at.htlwels.ires.control.AuthorizationViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.view.Routes
import at.htlwels.ires.view.VerticalSpacer

@Composable
fun RegisterScreen(
    authModel: AuthorizationViewModel,
    navToRoute: (Routes.Authorization) -> Unit,
    usernameLogin: String,
    passwordLogin: String
){
    val signupState by authModel.signupState

    val firstName = remember { mutableStateOf("")}
    val userName = remember { mutableStateOf(usernameLogin)}
    val password = remember { mutableStateOf(passwordLogin)}
    val email = remember { mutableStateOf("")}

    val websiteRef = buildAnnotatedString {
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )){
            withLink(LinkAnnotation.Url(url = "https://www.google.com")){
                append("Veranstalter Login")
            }
        }
    }

    if(signupState is Resource.Success){
        authModel.resetSignupState()
        navToRoute(Routes.Authorization.Authorized)
    } else {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.signup_background),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )

            Card (
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.8f),
                elevation = CardDefaults.cardElevation(defaultElevation = 64.dp)
            ) {
                Column (modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(40.dp)
                ) {
                    BackButtonRow( onClick = {navToRoute(Routes.Authorization.LoginScreen)}) {
                        Text(websiteRef)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Registrieren", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        VerticalSpacer(32)

                        RegisterField(
                            icon = { Icon(Icons.Default.Star, null) },
                            text = userName,
                            label = "Benutzername",
                            resetError = authModel::resetSignupState
                        )
                        VerticalSpacer(8)

                        RegisterField(
                            icon = { Icon(Icons.Default.Person, null) },
                            text = firstName,
                            label = "Vorname",
                            resetError = authModel::resetSignupState
                        )
                        VerticalSpacer(8)
                        RegisterField(
                            icon = { Icon(Icons.Default.Email, null) },
                            text = email,
                            label = "Email",
                            keyboardType = KeyboardType.Email,
                            resetError = authModel::resetSignupState
                        )
                        VerticalSpacer(8)
                        PasswordField(password, resetError = authModel::resetSignupState)

                        VerticalSpacer(32)

                        if(signupState is Resource.Loading){
                            CircularProgressIndicator()
                        } else {
                            FullWidthButton("Submit", onClick = {
                                authModel.signup(
                                    firstname = firstName.value,
                                    username = userName.value,
                                    password = password.value,
                                    email = email.value
                                )
                            })
                        }


                        val finalErrorState = signupState
                        if(finalErrorState is Resource.Error){
                            VerticalSpacer(16)
                            ErrorText(finalErrorState.getMessage())
                        }
                    }
                }
            }
        }
    }
}