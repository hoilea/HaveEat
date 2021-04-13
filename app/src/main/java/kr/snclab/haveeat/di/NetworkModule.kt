package kr.snclab.haveeat.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kr.snclab.haveeat.Define
import kr.snclab.haveeat.api.auth.AuthService
import kr.snclab.haveeat.api.diets.DietsService
import kr.snclab.haveeat.api.foods.FoodsService
import kr.snclab.haveeat.api.user.UsersService
import kr.snclab.haveeat.api.apiInterceptor
import kr.snclab.haveeat.api.views.ViewsService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseUrl() = Define.URL_HOST

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    fun provideOkHttpClient(logger:HttpLoggingInterceptor): OkHttpClient{
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logger)
        val okHttp = okHttpClient.build()
        return okHttp
    }

    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String,okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .apply {
                client(apiInterceptor)
            }
//            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit):AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideDietsService(retrofit: Retrofit):DietsService =
        retrofit.create(DietsService::class.java)

    @Provides
    @Singleton
    fun provideFoodsService(retrofit: Retrofit):FoodsService =
        retrofit.create(FoodsService::class.java)

    @Provides
    @Singleton
    fun provideUsersService(retrofit: Retrofit): UsersService =
        retrofit.create(UsersService::class.java)

    @Provides
    @Singleton
    fun provideViewsService(retrofit: Retrofit):ViewsService=
        retrofit.create(ViewsService::class.java)

}