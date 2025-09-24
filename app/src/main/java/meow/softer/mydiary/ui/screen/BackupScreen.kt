package meow.softer.mydiary.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BackupScreen() {
    BackupScreenContent()
}

@Composable
fun BackupScreenContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.statusBarsPadding()) {
        Text("Back up to be implemented")

    }
}