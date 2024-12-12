package at.htlwels.ires.view.main.tour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.htlwels.ires.model.dto.tour.Tour

@Composable
fun TourDetailScreen(tour: Tour){

    println("tour details")

    Column(modifier = Modifier.fillMaxSize()) {

        Text(tour.name, style = MaterialTheme.typography.headlineMedium)

        Text(tour.description)
        Text(tour.startDate)
    }
}