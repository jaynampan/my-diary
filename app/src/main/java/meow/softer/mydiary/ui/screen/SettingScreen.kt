package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.component.DiaryButton


@Composable
fun SettingScreen() {
    SettingScreenContent()
}

@Composable
fun SettingScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ThemeSetting()
        Spacer(Modifier.height(16.dp))
        LanguageSetting()
    }
}

@Composable
private fun ThemeSetting(modifier: Modifier = Modifier) {
    Column {
        Text(stringResource(R.string.setting_theme_hint))
        DropdownMenu(
            expanded = false,
            onDismissRequest = {}
        ) { }
        Image(
            painter = painterResource(R.drawable.profile_theme_bg_taki),
            contentDescription = null
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Main Color")
            DiaryButton(onClick = {}) {
                Text(stringResource(R.string.setting_theme_default_color))
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Secondary Color")
            DiaryButton(
                onClick = {}) {
                Text(stringResource(R.string.setting_theme_default_color))
            }
        }
        DiaryButton(
            modifier = Modifier
                .padding(horizontal = 48.dp)
                .fillMaxWidth(), onClick = {}) {
            Text("Apply")
        }
    }
}

@Composable
private fun LanguageSetting(modifier: Modifier = Modifier) {
    Column {
        Text(stringResource(R.string.setting_language_hint))
        DropdownMenu(
            expanded = false,
            onDismissRequest = {}
        ) { }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun SettingContentPreview() {
    SettingScreenContent()
}