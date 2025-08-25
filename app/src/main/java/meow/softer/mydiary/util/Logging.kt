package meow.softer.mydiary.util

import android.util.Log
import meow.softer.mydiary.BuildConfig


private val DEBUG = BuildConfig.DEBUG
private const val DEBUG_PREFIX = "^_^#"

fun debug(tag: String, msg: String) {
    if (DEBUG) {
        Log.d(tag.prefix(DEBUG_PREFIX), msg)
    }
}

fun error(tag: String, msg: String) {
    if (DEBUG) {
        Log.e(tag.prefix(DEBUG_PREFIX), msg)
    }
}

fun info(tag: String, msg: String) {
    if (DEBUG) {
        Log.i(tag.prefix(DEBUG_PREFIX), msg)
    }
}

private fun String.prefix(prefix: String): String {
    return prefix + this
}