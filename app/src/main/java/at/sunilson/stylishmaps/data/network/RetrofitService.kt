package at.sunilson.stylishmaps.data.network

import at.sunilson.stylishmaps.data.entities.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService {

    @GET("https://api.mapbox.com/geocoding/v5/mapbox.places/{query}.json?access_token=pk.eyJ1Ijoic3VuaWxzb24iLCJhIjoiY2sxbDEzdGFrMDB1bTNpbnlsYXlsZXRqdSJ9.UZdCqTVNC49WngBF7r_0eQ")
    fun searchLocation(@Path("query") query: String): Call<SearchResponse>

}