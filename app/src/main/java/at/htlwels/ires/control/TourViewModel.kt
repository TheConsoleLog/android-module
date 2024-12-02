package at.htlwels.ires.control

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class TourViewModel : ViewModel() {

    private val _tourStage = mutableStateOf(TourStage.Ready)
    val tourStage: State<TourStage> = _tourStage



    fun updateTourStage(new: TourStage){ _tourStage.value = new }


}

enum class TourStage{
    Ready,
    Creating,
    Joining,
    Running
}
