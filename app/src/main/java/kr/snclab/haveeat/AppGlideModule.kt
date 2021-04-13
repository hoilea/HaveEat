package kr.snclab.haveeat

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

private const val DISK_CHCHE_SIZE_BYTES = 1024L * 1024L * 30L // 300mb
private const val MEMORY_CHCHE_SIZE_BYTES = 1024L * 1024L * 30L // 30mb

@GlideModule
class AppGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setMemoryCache(LruResourceCache(MEMORY_CHCHE_SIZE_BYTES))
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, DISK_CHCHE_SIZE_BYTES))
        builder.setDefaultRequestOptions(requestOptions())
        builder.setLogLevel(Log.VERBOSE)
    }

    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        val client = OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
        val factory = OkHttpUrlLoader.Factory(client)
        glide.registry.replace(
            GlideUrl::class.java,
            InputStream::class.java, factory
        )
    }

    private fun requestOptions(): RequestOptions {
        return RequestOptions()
//                .override(600, 200)
//                .centerInside()
//                .encodeFormat(Bitmap.CompressFormat.PNG)
//                .encodeQuality(100)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .format(PREFER_ARGB_8888)
//                .skipMemoryCache(true)
    }
}