package meow.softer.mydiary.navigation

interface DiaryDestination {
    val route: String
}

object HomeScreen : DiaryDestination {
    override val route = "HomeScreen"
}

object ContactScreen : DiaryDestination {
    override val route = "ContactScreen"
}

object AboutScreen : DiaryDestination {
    override val route = "AboutScreen"
}

object SettingScreen : DiaryDestination {
    override val route = "SettingScreen"
}

object SecurityScreen : DiaryDestination {
    override val route = "SecurityScreen"
}

object BackupScreen : DiaryDestination {
    override val route = "BackupScreen"
}

object BottomSettingDialog : DiaryDestination {
    override val route = "BottomSettingDialog"
}

object ProfileDialog : DiaryDestination {
    override val route = "ProfileDialog"
}
