package kr.snclab.haveeat.util

import android.util.Log
import com.facebook.stetho.inspector.console.CLog
import com.facebook.stetho.inspector.console.ConsolePeerManager
import com.facebook.stetho.inspector.protocol.module.Console
import timber.log.Timber

class StethoTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        val peerManager = ConsolePeerManager.getInstanceOrNull() ?: return
        val logLevel: Console.MessageLevel = when (priority) {
            Log.VERBOSE, Log.DEBUG -> Console.MessageLevel.DEBUG
            Log.INFO -> Console.MessageLevel.LOG
            Log.WARN -> Console.MessageLevel.WARNING
            Log.ERROR, Log.ASSERT -> Console.MessageLevel.ERROR
            else -> Console.MessageLevel.LOG
        }
        CLog.writeToConsole(
            logLevel,
            Console.MessageSource.OTHER,
            message
        )
    }
}