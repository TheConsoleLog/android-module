package at.htlwels.ires.view.main.tour

import androidx.compose.runtime.Composable
import at.htlwels.ires.control.TourStage
import at.htlwels.ires.control.TourViewModel

@Composable
fun TourScreen(viewModel: TourViewModel){

    when(viewModel.tourStage.value){
        TourStage.Ready -> { NoTourScreen(viewModel) }
        TourStage.Creating -> { TourCreationScreen(viewModel) }
        TourStage.Running -> TODO()
        TourStage.Joining -> TODO()
    }
}

