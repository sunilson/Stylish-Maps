package at.sunilson.stylishmaps.data

import at.sunilson.stylishmaps.data.network.RetrofitService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single {
        Retrofit
            .Builder()
            .client(OkHttpClient())
            .baseUrl("https://www.google.at")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }
    single<Repository> { RepositoryImpl(get(), get()) }
}