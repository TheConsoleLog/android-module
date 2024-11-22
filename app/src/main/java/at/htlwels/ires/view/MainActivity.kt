package at.htlwels.ires.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import at.htlwels.ires.control.AuthorizationViewModel
import at.htlwels.ires.view.auth.ForgotPasswordScreen
import at.htlwels.ires.view.auth.LoginScreen
import at.htlwels.ires.view.auth.RegisterScreen
import at.htlwels.ires.view.main.MainScaffold
import at.htlwels.ires.view.theme.IresTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//TODO chatgpt login Hintergrund
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IresTheme {
                TopLevelNavigator()
            }
        }
    }
}

/** @see Routes */
@Composable
fun TopLevelNavigator(){

    val navController = rememberNavController()
    val authVM = viewModel<AuthorizationViewModel>()

    val loginScreenSnackBarState = remember { SnackbarHostState() }
    val snackBarScope = rememberCoroutineScope()

    fun setLoginSnackBar (message: String, delayMs: Long? = null){
        snackBarScope.launch {
            delay(delayMs ?: 0)
            loginScreenSnackBarState.showSnackbar(message)
        }
    }


    NavHost(navController, Routes.Authorization.LoginScreen){

        composable<Routes.Authorization.LoginScreen>{
            LoginScreen (
                vm = authVM,
                navToRoute = { route -> navController.navigate(route) },
                snackBarState = loginScreenSnackBarState,
                navToSignup = { username, password ->
                    navController.navigate(
                        Routes.Authorization.RegisterScreen(
                            username = username,
                            password = password
                        )
                    )
                }
            )
        }

        composable<Routes.Authorization.RegisterScreen>{
            val args = it.toRoute<Routes.Authorization.RegisterScreen>()
            RegisterScreen (
                authModel = authVM,
                navToRoute = { route -> navController.navigate(route) },
                usernameLogin = args.username,
                passwordLogin = args.password
            )
        }

        composable<Routes.Authorization.ForgotPWScreen>{
            ForgotPasswordScreen(
                viewModel = authVM,
                setLoginSnackBar = { msg: String, delay: Long? -> setLoginSnackBar(msg, delay) },
                navToRoute = { navController.navigate(it) }
            )
        }

        composable<Routes.Authorization.Authorized>{
            MainScaffold(logout = {
                authVM.logout()
                navController.navigate(Routes.Authorization.LoginScreen)
            })
        }
    }
}