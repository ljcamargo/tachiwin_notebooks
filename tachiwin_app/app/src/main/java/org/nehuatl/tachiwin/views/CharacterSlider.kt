package org.nehuatl.tachiwin.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nehuatl.tachiwin.firstUpper
import org.nehuatl.tachiwin.ui.theme.TachiwinTheme

@Composable
fun CharacterSlider(
    modifier: Modifier = Modifier,
    labels: List<String>,
    value: MutableState<Float>,
    onValueChange: (Int) -> Unit = {  }
) {
    val (sliderValue, setSliderValue) = value
    val drawPadding = with(LocalDensity.current) { 8.dp.toPx() }
    val textSize = with(LocalDensity.current) { 10.dp.toPx() }
    val canvasHeight = 72.dp
    val textPaint = android.graphics.Paint().apply {
        color = MaterialTheme.colorScheme.onBackground.toArgb()
        textAlign = android.graphics.Paint.Align.CENTER
        this.textSize = textSize
    }
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeight)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .height(canvasHeight)
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    val distance = (size.width.minus(2 * drawPadding) - 8)
                        .div(labels.size.minus(1))
                    labels.forEachIndexed { index, date ->
                        drawContext.canvas.nativeCanvas.drawText(
                            date.firstUpper(),
                            drawPadding + index.times(distance),
                            size.height / 2,
                            textPaint
                        )
                    }
                }
                Slider(
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .testTag("SLIDER_TAG")
                        .fillMaxWidth(),
                    value = sliderValue,
                    valueRange = 0f..labels.size.minus(1).toFloat(),
                    steps = labels.size.minus(2),
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                        activeTrackColor = secondary,
                        inactiveTrackColor = secondary
                    ),
                    onValueChange = {
                        setSliderValue(it)
                        onValueChange(it.toInt())
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SliderPreview() {
    val value = remember { mutableStateOf(0f) }
    TachiwinTheme(darkTheme = true) {
        CharacterSlider(
            modifier = Modifier.fillMaxWidth(),
            labels = listOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","Ñ","O","P","Q","R","S","T","U","W","X","Y","Z"),
            value = value,
            onValueChange = {}
        )
    }
}