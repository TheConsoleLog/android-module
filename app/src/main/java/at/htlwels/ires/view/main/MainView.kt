package at.htlwels.ires.view.main


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import at.htlwels.ires.control.MainViewModel
import at.htlwels.ires.view.Routes
import at.htlwels.ires.view.main.gallery.GalleryCameraPager
import at.htlwels.ires.view.main.tour.TourScreen

@Composable
fun MainScaffold(logout: () -> Unit){

    val navController = rememberNavController()
    val viewModel : MainViewModel = viewModel()

    val currentScreen by viewModel.currentScreen

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

            when (currentScreen){
                Routes.Main.TourScreen -> { }
                Routes.Main.ActivitiesScreen -> {}
                Routes.Main.GalleryScreen -> { }
                Routes.Main.ProfileScreen -> {}
            }
        },
        bottomBar = {
            MainBottomBar( navToRoute = {
                navController.navigate(it)
                viewModel.setCurrentScreen(it)
            },
                // pass current route to BottomBar to determine which icon should be highlighted
                route = viewModel.currentScreen,
                mainRoutesInfo = viewModel.mainRoutesInfo.keys
            )
        }
    ) { innerPadding ->

        MainScreenNavigator(
            navController,
            innerPadding,
            logout = logout
        )
    }
}

@Composable
private fun MainBottomBar(
    navToRoute: (Routes.Main) -> Unit,
    route: State<Routes.Main>,
    mainRoutesInfo: Set<Routes.Main>
){

    NavigationBar {
        mainRoutesInfo.forEach{

            NavigationBarItem(
                selected = it == route.value,
                label = { Text(it.name) },
                onClick = {
                    navToRoute(it)
                    println("Navigation to route $it")
                          },
                icon = { Icon(painter = painterResource(it.icon), contentDescription = null) }
            )
        }
    }
}


/**
 * Viel einfacher als vor Version 2.8 wuiiii
 *
 * See: [Type Safe Navigation with Compose Library](https://developer.android.com/jetpack/androidx/releases/navigation#2.8.0)
 */
@Composable
fun MainScreenNavigator(
    navController: NavHostController,
    pV: PaddingValues,
    logout: () -> Unit
){
    NavHost(
        navController = navController,
        startDestination = Routes.Main.TourScreen,
        modifier = Modifier.padding(pV),
    ) {

        composable <Routes.Main.TourScreen> {
            TourScreen(viewModel = viewModel())
        }

        composable<Routes.Main.ActivitiesScreen>{

        }

        composable<Routes.Main.GalleryScreen>{
            GalleryCameraPager()
        }

        composable<Routes.Main.ProfileScreen>{

        }

        composable<Routes.EventDetailScreen>{
            val args = it.toRoute<Routes.EventDetailScreen>()

        }
    }
}



