package org.nehuatl.tachiwin.views

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import org.nehuatl.tachiwin.Constants
import org.nehuatl.tachiwin.R
import org.nehuatl.tachiwin.agnostic.AgnosticAdView

@Composable
fun BannerAd(modifier:Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .defaultMinSize(minHeight = 52.dp),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().zIndex(1f),
                factory = { context: Context ->
                    AgnosticAdView(
                        context = context,
                        adSize = AgnosticAdView.AgnosticAdSize.BANNER,
                        adUnit = Constants.BANNER_ID
                    ).getAd()
                }
            )
            Text(
                text = stringResource(R.string.ads_rationale),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp).align(Alignment.Center)
            )
        }
    }
}