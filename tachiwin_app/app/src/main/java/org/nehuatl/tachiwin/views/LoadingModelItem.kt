package org.nehuatl.tachiwin.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.DevicePerformanceHelper
import org.nehuatl.tachiwin.R

@Composable
fun LoadingModelItem() {

    val context = LocalContext.current
    val performance = DevicePerformanceHelper.performance(context)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.loading_model),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.loading_model_rational),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(when (performance) {
                DevicePerformanceHelper.Performance.LOW -> R.string.performance_low
                DevicePerformanceHelper.Performance.MEDIUM -> R.string.performance_medium
                DevicePerformanceHelper.Performance.HIGH -> R.string.performance_high
            }),
            style = MaterialTheme.typography.bodySmall,
        )
        if (Constants.SHOW_ADS) BannerAd()
    }

}