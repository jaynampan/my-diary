package meow.softer.mydiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.R

@Composable
fun HomeHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_person_picture_default),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .border(2.dp, Color.White)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeadPreview(){
    HomeHeader()
}