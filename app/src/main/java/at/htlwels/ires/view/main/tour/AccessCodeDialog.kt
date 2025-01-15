package at.htlwels.ires.view.main.tour

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import at.htlwels.ires.R
import at.htlwels.ires.view.VerticalSpacer
import com.lightspark.composeqr.QrCodeView

@Composable
fun AccessCodeDialog(
    dismiss: () -> Unit,
    accessCode: String
){
    Dialog(
        onDismissRequest = dismiss
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_person_add_24),
                null,
                modifier = Modifier.size(40.dp)
            )
            VerticalSpacer(16)
            Text("Invite people", style = MaterialTheme.typography.headlineMedium)
            VerticalSpacer(8)
            Text("Use this code to add people to the group!")
            VerticalSpacer(16)
            QrCodeView(
                data = accessCode,
                modifier = Modifier
                    .size(250.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            )
            VerticalSpacer(16)
            Text("Access Code: $accessCode")
        }
    }
}