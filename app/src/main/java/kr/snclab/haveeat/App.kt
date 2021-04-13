package kr.snclab.haveeat

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import kr.snclab.haveeat.util.StethoTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
        SharedData.init(applicationContext)
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
            Timber.plant(StethoTree())
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
        firebaseAnalytics = Firebase.analytics
        KakaoSdk.init(this, "5e74afe7751b2e57d7558035ac9161bc")
    }
}