package meow.softer.mydiary.ui.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AboutScreen() {
    val license = StringBuilder()
    license.append(
        "\t\t\tDear User:\n" +
                "\t\t\tThis small app is coded based on Daxiak's MyDiary on github.\n" +
                "\t\t\tThe app still has some bugs...Hope you don't mind.\n" +
                "\t\t\tHope you enjoy using this diary app and record your life here :).\n\n" +
                "\t\t\tNOTE: you can set your password to keep your diary safe.\n"
    )

    Text(text = license.toString())
}