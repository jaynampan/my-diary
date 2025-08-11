package meow.softer.mydiary.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import meow.softer.mydiary.R


@Composable
fun MainSettingDialogWrapper(modifier: Modifier = Modifier) {

}
@Composable
fun MainSettingDialog(
    bgColor: Color,
    hasPassword: Boolean = false,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .background(bgColor),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Icon(modifier = Modifier
            .clickable{onClick("Add")},
            painter = painterResource(R.drawable.ic_add_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable{onClick("Setting")},
            painter = painterResource(R.drawable.ic_settings_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable{onClick("Lock")},
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
                .clickable{onClick("Backup")},
            painter = painterResource(R.drawable.ic_backup_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .clickable{onClick("About")},
            painter = painterResource(R.drawable.ic_perm_device_information_white_36dp),
            tint = Color.White,
            contentDescription = null
        )
    }
}