package meow.softer.mydiary.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DiaryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier
            .background(color = Color.Transparent),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xffb2b2b2)
        ),
        border = BorderStroke(Dp.Hairline, Color(0xFF103E6C)),
        onClick = { onClick() }
    ) {
        content()
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ButtonPreview() {
    DiaryButton(
        onClick = {}
    ) { Text("tessdfsdftdsf", color = Color.Red) }
}