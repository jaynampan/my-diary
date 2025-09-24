package meow.softer.mydiary.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.R
import meow.softer.mydiary.navigation.AboutScreen
import meow.softer.mydiary.navigation.BackupScreen
import meow.softer.mydiary.navigation.SecurityScreen
import meow.softer.mydiary.navigation.SettingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSettingSheet(
    onDismiss: () -> Unit,
    hasPassword: Boolean = false,
    onClick: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        dragHandle = null
    ) {
        BottomSettingDialog(hasPassword = hasPassword) { onClick(it) }
    }
}


@Composable
fun BottomSettingDialog(
    hasPassword: Boolean = false,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 32.dp) // todo:adjust
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(
            modifier = Modifier
                .clickable { onClick("Add") },
            painter = painterResource(R.drawable.ic_add_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable { onClick(SettingScreen.route) },
            painter = painterResource(R.drawable.ic_settings_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable { onClick(SecurityScreen.route) },
            painter = painterResource(
                if (hasPassword) {
                    R.drawable.ic_enhanced_encryption_white_36dp
                } else {
                    R.drawable.ic_no_encryption_white_36dp
                }
            ),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable { onClick(BackupScreen.route) },
            painter = painterResource(R.drawable.ic_backup_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable { onClick(AboutScreen.route) },
            painter = painterResource(R.drawable.ic_perm_device_information_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
    }
}