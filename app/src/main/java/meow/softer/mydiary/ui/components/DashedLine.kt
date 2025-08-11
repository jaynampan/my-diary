package meow.softer.mydiary.ui.components

//import android.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun DashedLine(
    modifier: Modifier
) {
    val density = LocalDensity.current.density // Get the current device density

    Canvas(modifier = modifier.fillMaxWidth().height(20.dp)) {
        val width = size.width  // Get the full screen width
        val path = Path().apply {
            moveTo(0f, size.height / 2)  // Start at the center of the height (20dp)
            lineTo(width, size.height / 2)  // Draw a line across the full width
        }

        // Scale the dash length and spacing using density
        val dashLength = 4.dp.toPx() * density // Convert to pixels
        val dashSpacing = 4.dp.toPx() * density // Convert to pixels

        drawPath(
            path = path,
            color = Color.Black,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1f * density, // Scale stroke width as well
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, dashSpacing), 0f),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}