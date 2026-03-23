package meow.softer.mydiary.data.local.store

enum class AppTheme { TAKI, MITSUHA, CUSTOM }
enum class AppLanguage { EN, ZH }
const val DEFAULT_USERNAME = "Taki"
data class AppSettings(
    val theme: AppTheme = AppTheme.TAKI,
    val language : AppLanguage = AppLanguage.EN,
    val username : String = DEFAULT_USERNAME, // default username
    val isSecurityEnabled: Boolean = false
)