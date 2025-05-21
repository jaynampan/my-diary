package meow.softer.mydiary.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonDialog(
    modifier: Modifier = Modifier,
    content: String = "",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = content)
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DiaryButton(onClick = { onCancel() }) {
                Text("Cancel")
            }
            DiaryButton(onClick = { onConfirm() }) {
                Text("Confirm")
            }
        }
    }
}