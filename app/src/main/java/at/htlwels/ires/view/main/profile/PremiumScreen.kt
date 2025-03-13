package at.htlwels.ires.view.main.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.htlwels.ires.common.Constants
import at.htlwels.ires.control.PremiumViewModel
import at.htlwels.ires.view.HorizontalSpacer
import at.htlwels.ires.view.VerticalSpacer
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyPremiumScreen(
    updateTopBar: (@Composable () -> Unit) -> Unit,
    navBack: () -> Unit,
    viewModel: PremiumViewModel
){
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        updateTopBar{
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = navBack
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                    }
                },
                title = { Text("Purchase Ires Premium") }
            )
        }

        PaymentConfiguration.init(context, Constants.STRIPE_API_KEY)
    }

    var loading by remember { mutableStateOf(false) }

    val paymentSheet = rememberPaymentSheet {
        val toastMessage = when(it){
            is PaymentSheetResult.Completed -> "Purchased Ires Premium!"
            is PaymentSheetResult.Failed -> "Payment Failed: ${it.error.message}."
            is PaymentSheetResult.Canceled -> "Payment canceled."
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    }


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            "Go Premium",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        VerticalSpacer(16)
        Text(
            "Support the creators and unlock exclusive benefits by purchasing a premium account.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        VerticalSpacer(24)

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            PremiumBenefitItem("Ad-free experience")
            PremiumBenefitItem("Early access to new features")
            PremiumBenefitItem("Exclusive premium content")
            PremiumBenefitItem("Lifetime subscription")
        }

        VerticalSpacer(24)

        Text(
            "Only 12€ now!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        VerticalSpacer(16)

        if(loading){
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    loading = true
                    viewModel.createPaymentIntent(
                        2,"eur", //TODO
                        onResult = { clientSecret ->
                            loading = false
                            paymentSheet.presentWithPaymentIntent(clientSecret, PaymentSheet.Configuration(
                                merchantDisplayName = "App Creators",
                                allowsDelayedPaymentMethods = true
                            ))
                        }
                    )
                }
            ) {
                Text("Buy now")
            }
        }
    }
}


@Composable
fun PremiumBenefitItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        HorizontalSpacer(8)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
