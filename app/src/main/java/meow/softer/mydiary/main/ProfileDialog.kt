package meow.softer.mydiary.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.components.DiaryButton
import meow.softer.mydiary.ui.home.MainViewModel
import meow.softer.mydiary.ui.theme.primaryLight

@Composable
fun ProfileDialogWrapper(
    mainViewModel: MainViewModel,
    onClick: (String) -> Unit
) {

    val profilePainter = mainViewModel.userPainter.collectAsStateWithLifecycle().value
        ?: painterResource(R.drawable.ic_person_picture_default)
    val userName = mainViewModel.userName.collectAsStateWithLifecycle().value
    ProfileDialog(
        painter = profilePainter,
        userName = userName ,
        onDismiss = { onClick("Dismiss") },
        onConfirm = { onClick("Confirm") },
        onChooseProfile = { onClick("Photo") },
        onResetProfile = { onClick("Reset") },
        updateUserName = {mainViewModel.updateUserName(it)},
    )

}

@Composable
fun ProfileDialog(
    painter: Painter,
    userName:String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onChooseProfile: () -> Unit,
    onResetProfile: () -> Unit,
    updateUserName: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .height(250.dp)
            .width(300.dp)
            .background(primaryLight, RoundedCornerShape(3.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, shape = CircleShape)
                        .clickable { onChooseProfile() }
                )
                Image(
                    modifier = Modifier.clickable { onResetProfile() },
                    painter = painterResource(R.drawable.ic_cancel_black_24dp),
                    contentDescription = null
                )
            }

            Spacer(Modifier.height(10.dp))
            var textValue by remember { mutableStateOf(userName) }
            TextField(
                value = textValue,
                onValueChange = { textValue = it },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White
                ),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DiaryButton(
                    modifier = Modifier,
                    onClick = { onDismiss() }
                ) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(30.dp))
                DiaryButton(
                    modifier = Modifier,
                    onClick = {
                        updateUserName(textValue)
                        onConfirm()
                    }
                ) {
                    Text("Confirm")
                }
            }

        }
    }

}

