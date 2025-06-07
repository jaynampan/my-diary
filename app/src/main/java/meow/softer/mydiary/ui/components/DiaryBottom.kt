package meow.softer.mydiary.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.ui.theme.primaryLight


@Composable
fun DiaryBottom(
    modifier: Modifier = Modifier,
    @DrawableRes images: List<Int>,
    onClick: (Int) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(primaryLight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Spacer(Modifier.width(20.dp))
        images.forEachIndexed { idx, resId ->
            Image(
                painter = painterResource(resId),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onClick(idx) }
            )
        }
        Spacer(Modifier.width(20.dp))

    }
}