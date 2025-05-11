package meow.softer.mydiary.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.home.MainViewModel
import meow.softer.mydiary.ui.theme.primaryLight

@Composable
fun ProfileDialogWrapper(
    mainViewModel: MainViewModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    val profilePainter = mainViewModel.userPainter.collectAsStateWithLifecycle().value
        ?: painterResource(R.drawable.ic_person_picture_default)
    ProfileDialog(
        painter = profilePainter,
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm() }
    )

}

@Composable
fun ProfileDialog(
    painter: Painter,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(200.dp)
            .width(300.dp)
            .background(primaryLight, RoundedCornerShape(6.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, shape = CircleShape)
            )
            var textValue by remember { mutableStateOf("") }
            TextField(
                value = textValue,
                onValueChange = { textValue = it })
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier,
                    onClick = { onDismiss() }
                ) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier,
                    onClick = { onConfirm() }
                ) {
                    Text("Confirm")
                }
            }

        }
    }

}