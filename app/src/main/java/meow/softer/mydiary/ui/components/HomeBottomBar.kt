package meow.softer.mydiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.theme.primaryLight

@Composable
fun HomeBottomBar(
    onSettingClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DiarySearchBar(Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .padding(end = 12.dp)
                .clickable { onSettingClick() }
                .testTag("home_bottom_bar_setting"),
            painter = painterResource(R.drawable.ic_settings_black_24dp),
            contentDescription = "Setting Icon",
            tint = primaryLight
        )
    }
}

@Composable
fun DiarySearchBar(
    //todo: height bug
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    // todo: update to searchbar and implement search function
    TextField(
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
            .background(primaryLight, RoundedCornerShape(50f)),
        value = textInput,
        onValueChange = { textInput = it },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search_white_18dp),
                contentDescription = null, tint = Color.White
            )
        },
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            cursorColor = primaryLight,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = primaryLight,
            unfocusedContainerColor = primaryLight
        ),
        shape = RoundedCornerShape(50F)
    )
}

