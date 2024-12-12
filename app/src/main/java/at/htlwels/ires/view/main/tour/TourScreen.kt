package at.htlwels.ires.view.main.tour

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import at.htlwels.bonfire.view.auth.ErrorText
import at.htlwels.ires.control.TourViewModel
import at.htlwels.ires.model.Resource
import at.htlwels.ires.view.ProgressIndicatorBox
import retrofit2.HttpException

@Composable
fun TourScreen(viewModel: TourViewModel){

    LaunchedEffect(Unit) {
        viewModel.fetchUserTour()
    }

    when(val tourstage = viewModel.tourStage.value){
        is Resource.Ready -> {  }
        is Resource.Loading -> { ProgressIndicatorBox() }
        is Resource.Success -> { TourDetailScreen(tourstage.data.tour) }
        is Resource.Error -> {
            if(tourstage.details is HttpException && tourstage.details.code() == 404){
                NoTourScreen(viewModel)
            } else{
                ErrorText(tourstage.getMessage())
            }
        }
    }
}

