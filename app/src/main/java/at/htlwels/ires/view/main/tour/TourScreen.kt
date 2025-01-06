package at.htlwels.ires.view.main.tour

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import at.htlwels.bonfire.view.auth.ErrorText
import at.htlwels.ires.control.NoTourScreen
import at.htlwels.ires.control.NoTourViewModel
import at.htlwels.ires.control.TourViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.view.ProgressIndicatorBox
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourScreen(
    viewModel: TourViewModel,
    updateTopBar: (@Composable () -> Unit) -> Unit
){

    updateTopBar { TopAppBar(
        title = { Text("Tour")}
    )}

    LaunchedEffect(Unit) {
        viewModel.fetchUserTour()
    }

    // Usage of screen navigation using "when" instead of proper navigation due to need for passing Tour between Screens

    when(val tourstage = viewModel.tourStage.value){
        is Resource.Ready -> {  }
        is Resource.Loading -> { ProgressIndicatorBox() }
        is Resource.Success -> { TourDetailScreen(
            tour = tourstage.data.tour,
            updateTopBar = updateTopBar,
            reloadTourState = viewModel::fetchUserTour
        ) }
        is Resource.Error -> {
            if(tourstage.details is HttpException && tourstage.details.code() == 404){

                val noTourVM = viewModel<NoTourViewModel>()

                when(noTourVM.currentScreen.value){
                    NoTourScreen.Default -> {
                        NoTourScreen(
                            viewModel = noTourVM,
                            tryToFetchAgain = viewModel::fetchUserTour
                        )
                    }
                    NoTourScreen.Joining -> {
                        JoinTourScreen(
                            viewModel = noTourVM,
                            onSuccess = viewModel::newTourReceived,
                            updateTopBar = updateTopBar,
                            navigateBack = { noTourVM.currentScreen.value = NoTourScreen.Default }
                        )
                    }
                    NoTourScreen.Creating -> {
                        TourCreationScreen(
                            noTourVM,
                            onSuccess = viewModel::newTourReceived,
                            navigateBack = { noTourVM.currentScreen.value = NoTourScreen.Default },
                            updateTopBar = updateTopBar
                        )
                    }
                }
            } else{
                ErrorText(tourstage.getMessage())
            }
        }
    }
}

