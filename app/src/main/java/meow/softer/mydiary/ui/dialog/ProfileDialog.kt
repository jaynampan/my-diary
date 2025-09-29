package meow.softer.mydiary.ui.dialog

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tanishranjan.cropkit.CropController
import com.tanishranjan.cropkit.CropDefaults
import com.tanishranjan.cropkit.CropOptions
import com.tanishranjan.cropkit.CropRatio
import com.tanishranjan.cropkit.CropShape
import com.tanishranjan.cropkit.GridLinesType
import com.tanishranjan.cropkit.ImageCropper
import com.tanishranjan.cropkit.rememberCropController
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.component.DiaryButton
import meow.softer.mydiary.ui.screen.HomeViewModel
import meow.softer.mydiary.ui.theme.primaryLight
import kotlin.math.max

@Composable
fun ProfileDialogWrapper(
    homeViewModel: HomeViewModel,
    onClick: (String) -> Unit
) {

    val profilePainter = homeViewModel.userPainter.collectAsStateWithLifecycle().value
        ?: painterResource(R.drawable.ic_person_picture_default)
    val userName = homeViewModel.userName.collectAsStateWithLifecycle().value
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            homeViewModel.updateUserProfilePic(it)
        }
    }
    val isCroppingState = homeViewModel.isCroppingState.collectAsStateWithLifecycle().value
    val mBitmap: Bitmap? = homeViewModel.croppingBitmap.collectAsStateWithLifecycle().value
    var cropController: CropController?
    if (isCroppingState && mBitmap != null) {
        cropController = rememberCropController(
            bitmap = mBitmap,
            cropOptions = CropDefaults.cropOptions(
                cropShape = CropShape.AspectRatio(CropRatio.SQUARE),
                gridLinesType = GridLinesType.GRID_AND_CIRCLE
            )
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageCropper(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                cropController = cropController
            )
            Button(
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .fillMaxWidth(),
                onClick = {
                    val bitmap = cropController.crop()
                    homeViewModel.updateCroppedUserProfile(bitmap)
                    homeViewModel.closeCropping()
                }) {
                Text("Crop")
            }
        }
    } else {
        ProfileDialog(
            painter = profilePainter,
            userName = userName,
            onDismiss = { onClick("Dismiss") },
            onConfirm = { onClick("Confirm") },
            onChooseProfile = { launcher.launch("image/*") },
            onResetProfile = { onClick("Reset") },
            updateUserName = { homeViewModel.updateUserName(it) },
        )
    }
}

@Composable
fun ProfileDialog(
    painter: Painter,
    userName: String,
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
                    contentScale = ContentScale.Crop,
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

