package kr.snclab.haveeat.extension

import android.content.res.AssetManager

fun AssetManager.asString(path: String): String = open(path).bufferedReader().use { it.readText() }