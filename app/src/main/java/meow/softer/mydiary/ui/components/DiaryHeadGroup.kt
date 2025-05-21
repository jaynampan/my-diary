package meow.softer.mydiary.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DiaryHeadGroup(
    modifier: Modifier = Modifier,
    names: List<String>,
    selectedIdx: Int ,
    onClick: (Int) -> Unit
) {
    //todo: fix the head bar
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        names.forEachIndexed { idx, name ->
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = idx == selectedIdx,
                    onClick = {
                        onClick(idx)
                    }
                )
                Text(name)
            }
        }
    }
}

