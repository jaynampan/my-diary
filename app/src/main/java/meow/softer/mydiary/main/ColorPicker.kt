/**
 * Thanks to Abhilash and his article
 * https://proandroiddev.com/color-picker-in-compose-f8c29744705
 */
package meow.softer.mydiary.main

import android.graphics.Bitmap
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import meow.softer.mydiary.util.info
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint as AndroidPaint

private const val TAG = "ColorPicker"

@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    onColorChange: (Triple<Float, Float, Float>) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val hsv = remember {
            val hsv = floatArrayOf(0f, 0f, 0f)
            AndroidColor.colorToHSV(Color.Blue.toArgb(), hsv)
            mutableStateOf(
                Triple(hsv[0], hsv[1], hsv[2])
            )
        }
        SatValPanel(hue = hsv.value.first) { sat, value ->
            hsv.value = Triple(hsv.value.first, sat, value)
            onColorChange(hsv.value)
        }
        Spacer(modifier = Modifier.height(32.dp))
        HueBar { hue ->
            hsv.value = Triple(hue, hsv.value.second, hsv.value.third)
            onColorChange(hsv.value)
        }
    }
}

@Composable
fun HueBar(
    modifier: Modifier = Modifier,
    onSetHue: (Float) -> Unit
) {
    val pressOffset = remember {
        mutableStateOf(Offset.Zero)
    }
    val scope = rememberCoroutineScope()
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Canvas(
        modifier = modifier
            .width(300.dp)
            .height(40.dp)
            .emitDragGesture(interactionSource)
            .clip(RoundedCornerShape(50))
    )
    {
        val drawScopeSize = size
        val bitmap = createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = AndroidCanvas(bitmap)
        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val hueColors = IntArray(huePanel.width().toInt())
        val strip = 360f / hueColors.size
        for (i in hueColors.indices) {
            hueColors[i] = AndroidColor.HSVToColor(floatArrayOf(i * strip, 1f, 1f))
        }

        // drawing each line of hue color
        val linePaint = AndroidPaint()
        linePaint.strokeWidth = 0f // hairline width
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            hueCanvas.drawLine(
                i.toFloat(),
                0f,
                i.toFloat(),
                huePanel.bottom,
                linePaint
            )
        }
        drawBitmap(
            bitmap = bitmap,
            panel = huePanel
        )

        fun pointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x = when {
                pointX < huePanel.left -> 0F
                pointX > huePanel.right -> width
                else -> pointX - huePanel.left
            }
            return x * 360f / width
        }

        scope.collectForPress(interactionSource) { pressPosition ->
            // capture the size by drawScopeSize and use it inside this lambda closure to avoid recomposition
            // or bad timing caused issues
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            pressOffset.value = Offset(pressPos, 0f)
            info(TAG, "position: $pressPos , $pressPosition")
            val selectedHue = pointToHue(pressPos)
            onSetHue(selectedHue)
        }

        drawCircle(
            color = Color.White,
            radius = size.height / 2,
            center = Offset(pressOffset.value.x, size.height / 2),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun SatValPanel(
    hue: Float,
    setSatVal: (Float, Float) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    var sat: Float
    var value: Float
    val pressOffset = remember {
        mutableStateOf(Offset.Zero)
    }
    Canvas(
        modifier = Modifier
            .size(300.dp)
            .emitDragGesture(interactionSource)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val cornerRadius = 12.dp.toPx()
        val satValSize = size
        val bitmap = createBitmap(size.width.toInt(), size.height.toInt())
        val canvas = AndroidCanvas(bitmap)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val rgb = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
        val satShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
            -0x1, rgb, Shader.TileMode.CLAMP
        )
        val valShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
            -0x1, -0x1000000, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(
            satValPanel,
            cornerRadius,
            cornerRadius,
            AndroidPaint().apply {
                shader = ComposeShader(
                    valShader,
                    satShader,
                    PorterDuff.Mode.MULTIPLY
                )
            }
        )
        drawBitmap(
            bitmap = bitmap,
            panel = satValPanel
        )

        fun pointToSatVal(pointX: Float, pointY: Float): Pair<Float, Float> {
            val width = satValPanel.width()
            val height = satValPanel.height()
            val x = when {
                pointX < satValPanel.left -> 0f
                pointX > satValPanel.right -> width
                else -> pointX - satValPanel.left
            }
            val y = when {
                pointY < satValPanel.top -> 0f
                pointY > satValPanel.bottom -> height
                else -> pointY - satValPanel.top
            }
            val satPoint = 1f / width * x
            val valuePoint = 1f - 1f / height * y
            return satPoint to valuePoint
        }
        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPositionOffset = Offset(
                pressPosition.x.coerceIn(0f..satValSize.width),
                pressPosition.y.coerceIn(0f..satValSize.height)
            )

            pressOffset.value = pressPositionOffset
            val (satPoint, valuePoint) = pointToSatVal(pressPositionOffset.x, pressPositionOffset.y)
            sat = satPoint
            value = valuePoint
            setSatVal(sat, value)
        }
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = pressOffset.value,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )
        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = pressOffset.value,
        )

    }
}

fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource,
    setOffset: (Offset) -> Unit
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)
                ?.pressPosition
                ?.let(setOffset)
        }
    }
}

private fun Modifier.emitDragGesture(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val scope = rememberCoroutineScope()
    pointerInput(Unit) {
        detectDragGestures { input, _ ->
            scope.launch {
                interactionSource.emit(PressInteraction.Press(input.position))
            }
        }
    }.clickable(interactionSource, null) {
    }
}

private fun DrawScope.drawBitmap(
    bitmap: Bitmap,
    panel: RectF
) {
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap,
            null,
            panel,
            null
        )
    }
}

@Preview
@Composable
private fun ColorPickerPreview() {
    val hsv = remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
    Column {
        ColorPicker {
            val data = floatArrayOf(it.first, it.second, it.third)
            hsv.value = data
        }
        Text(
            text = "Hue: ${hsv.value[0]}, Sat: ${hsv.value[1]}, Val: ${hsv.value[2]}",
            fontSize = 36.sp
        )
    }

}

@Preview
@Composable
private fun SatValPanelPreview() {
    SatValPanel(18f) { _, _ -> }
}

@Preview
@Composable
private fun HueBarPreview() {
    HueBar {

    }
}
