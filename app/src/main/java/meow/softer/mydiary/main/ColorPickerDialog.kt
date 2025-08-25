package meow.softer.mydiary.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.ui.components.DiaryButton


@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    onConfirm: (FloatArray) -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val hsv = remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
        ColorPicker {
            hsv.value = floatArrayOf(it.first, it.second, it.third)
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier= Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DiaryButton(onClick = {
                onCancel()
            }) {
                Text("Cancel")
            }
            DiaryButton(onClick = {
                onConfirm(hsv.value)
            }) {
                Text("OK")
            }
        }
    }
}