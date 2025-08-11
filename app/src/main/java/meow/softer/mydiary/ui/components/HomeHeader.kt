package meow.softer.mydiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeHeader(
    profilePic: Painter,
    bgPainter: Painter,
    userName: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable{ onClick()},
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            painter = bgPainter,
            contentDescription = "profile background picture",
            modifier = Modifier
                .fillMaxSize()
                .testTag("home_header_bg")
        )
        UserProfileArea(
            modifier = Modifier
                .padding(start = 20.dp),
            painter = profilePic,
            text = userName
        )
    }
}

@Composable
fun UserProfileArea(
    modifier : Modifier= Modifier,
    painter: Painter,
    text: String = "User"
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, shape = CircleShape)
        )
        Spacer(
            modifier = Modifier
                .width(10.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color.White,
            style = TextStyle(
                shadow = Shadow(color = Color.Black, offset = Offset(2f, 2f), blurRadius = 3f)
            )
        )
    }
}

