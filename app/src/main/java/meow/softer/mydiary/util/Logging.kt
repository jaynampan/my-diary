package meow.softer.mydiary.util

import android.util.Log
import meow.softer.mydiary.BuildConfig


private const val DEBUG = BuildConfig.DEBUG
fun debug(tag:String, msg:String) {
    if(DEBUG){
        Log.d(tag, msg)
    }
}

fun error(tag:String, msg:String) {
    if (DEBUG){
        Log.e(tag, msg)
    }
}

fun info(tag:String, msg:String) {
    if (DEBUG){
        Log.i(tag, msg)
    }
}