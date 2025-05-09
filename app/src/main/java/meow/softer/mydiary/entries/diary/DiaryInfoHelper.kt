package meow.softer.mydiary.entries.diary

import meow.softer.mydiary.R

object DiaryInfoHelper {
    const val WEATHER_SIZE: Int = 6
    const val WEATHER_SUNNY: Int = 0
    const val WEATHER_CLOUD: Int = 1
    const val WEATHER_WINDY: Int = 2
    const val WEATHER_RAINY: Int = 3
    const val WEATHER_SNOWY: Int = 4
    const val WEATHER_FOGGY: Int = 5


    const val MOOD_SIZE: Int = 3
    const val MOOD_HAPPY: Int = 0
    const val MOOD_SOSO: Int = 1
    const val MOOD_UNHAPPY: Int = 2


    /**
     * Weather
     */
    @JvmStatic
    fun getWeatherResourceId(weather: Int): Int {
        val weatherResourceId: Int = when (weather) {
            WEATHER_CLOUD -> R.drawable.ic_weather_cloud
            WEATHER_WINDY -> R.drawable.ic_weather_windy
            WEATHER_RAINY -> R.drawable.ic_weather_rainy
            WEATHER_SNOWY -> R.drawable.ic_weather_snowy
            WEATHER_FOGGY -> R.drawable.ic_weather_foggy
            else -> R.drawable.ic_weather_sunny
        }
        return weatherResourceId
    }


    @JvmStatic
    val weatherArray: Array<Int?>
        get() = arrayOf<Int>(
            R.drawable.ic_weather_sunny, R.drawable.ic_weather_cloud,
            R.drawable.ic_weather_windy, R.drawable.ic_weather_rainy, R.drawable.ic_weather_snowy,
            R.drawable.ic_weather_foggy
        ) as Array<Int?>


    /**
     * Mood
     */
    @JvmStatic
    fun getMoodResourceId(mood: Int): Int {
        val moodResourceId: Int = when (mood) {
            MOOD_SOSO -> R.drawable.ic_mood_soso
            MOOD_UNHAPPY -> R.drawable.ic_mood_unhappy
            else -> R.drawable.ic_mood_happy
        }
        return moodResourceId
    }

    @JvmStatic
    val moodArray: Array<Int?>
        get() = arrayOf<Int>(
            R.drawable.ic_mood_happy, R.drawable.ic_mood_soso,
            R.drawable.ic_mood_unhappy
        ) as Array<Int?>
}
