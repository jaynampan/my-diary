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

object MemoScreen : DiaryDestination {
    override val route = "MemoScreen"
    const val routeWithArgs = "MemoScreen/{topicId}/{topicName}"
    const val topicIdArg = "topicId"
    const val topicNameArg = "topicName"
}

object DiaryScreen : DiaryDestination {
    override val route = "DiaryScreen"
    const val routeWithArgs = "DiaryScreen/{topicId}/{topicName}"
    const val topicIdArg = "topicId"
    const val topicNameArg = "topicName"
}


object BottomSettingDialog : DiaryDestination {
    override val route = "BottomSettingDialog"
}

object ProfileDialog : DiaryDestination {
    override val route = "ProfileDialog"
}

object ContactDetailDialog : DiaryDestination {
    override val route = "ContactDetailDialog"
}

object ColorPickerDialog : DiaryDestination {
    override val route = "ColorPickerDialog"
}
object AddTopicDialog : DiaryDestination {
    override val route = "AddTopicDialog"
}
