package at.htlwels.ires.view.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.htlwels.bonfire.view.auth.AuthButton
import at.htlwels.bonfire.view.auth.ErrorText
import at.htlwels.bonfire.view.auth.PasswordField
import at.htlwels.ires.R
import at.htlwels.ires.control.AuthorizationViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.view.Routes
import at.htlwels.ires.view.VerticalSpacer

@Composable
fun LoginScreen(
    vm: AuthorizationViewModel,
    navToSignup: (username: String, password: String) -> Unit,
    navToRoute: (route: Routes.Authorization) -> Unit,
    snackBarState: SnackbarHostState
){
    val loginState by vm.loginState

    var username by remember { mutableStateOf("")}
    val password = remember { mutableStateOf("")}

    if(loginState is Resource.Success){
        vm.resetLoginState()
        navToRoute(Routes.Authorization.Authorized)
    } else {
        Column (modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(R.drawable.landing_page_image),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Willkommen",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    VerticalSpacer(32)

                    OutlinedTextField(
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                        label = { Text("Benutzername") },
                        value = username,
                        onValueChange = { username = it; vm.resetLoginState() }
                    )

                    VerticalSpacer(16)

                    PasswordField(password, resetError = vm::resetLoginState)

                    VerticalSpacer(4)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { navToRoute(Routes.Authorization.ForgotPWScreen) }) {
                            Text("Passwort vergessen?")
                        }
                    }


                    VerticalSpacer(16)

                    if (loginState is Resource.Loading) {
                        CircularProgressIndicator()
                    } else {
                        AuthButton("Anmelden") {
                            vm.login(username, password.value)
                        }
                    }


                    val currentLoginState = loginState
                    if (currentLoginState is Resource.Error) {
                        VerticalSpacer(16)
                        ErrorText(currentLoginState.message)
                    } else {
                        VerticalSpacer(8)
                    }

                    VerticalSpacer(16)

                    Box(contentAlignment = Alignment.Center) {
                        HorizontalDivider()
                        Text(
                            color = MaterialTheme.colorScheme.onBackground,
                            text = "oder",
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(8.dp)
                        )
                    }

                    VerticalSpacer(24)

                    AuthButton("Registrieren") {
                        navToSignup(username, password.value)
                    }
                }

                SnackbarHost(
                    hostState = snackBarState,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                    snackbar = {
                        Snackbar(
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text(it.visuals.message)
                                Icon(Icons.Default.CheckCircle, null)
                            }
                        }
                    }
                )
            }
        }
    }
}