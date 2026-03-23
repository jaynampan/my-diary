package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.theme.primaryLight

@Composable
fun SecurityScreen(viewModel: SecurityViewModel) {
    val settings by viewModel.settings.collectAsState()
    SecurityScreenContent(
        isSecurityEnabled = settings.isSecurityEnabled,
        onSecurityToggle = { viewModel.setSecurityEnabled(it) }
    )
}

@Composable
fun SecurityScreenContent(
    isSecurityEnabled: Boolean,
    onSecurityToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Security",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = primaryLight
            )
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(24.dp))

        // Illustration/Icon
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_enhanced_encryption_white_36dp),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(primaryLight)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Protect your privacy",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "Use phone unlocking method to open app",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Toggle Item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSecurityToggle(!isSecurityEnabled) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "App Lock",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = "Unlock with fingerprint or PIN",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Switch(
                checked = isSecurityEnabled,
                onCheckedChange = onSecurityToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = primaryLight,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}