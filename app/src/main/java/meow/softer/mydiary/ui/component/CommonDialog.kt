package meow.softer.mydiary.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.ui.theme.primaryLight

/**
 * Common Dialog with Cancel and Confirm buttons
 * The text on buttons are 'Cancel' and 'Confirm'
 * */
@Composable
fun CommonDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .background(primaryLight, RoundedCornerShape(3.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
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
            Spacer(Modifier.width(30.dp))
            DiaryButton(onClick = { onConfirm() }) {
                Text("Confirm")
            }
        }
    }
}