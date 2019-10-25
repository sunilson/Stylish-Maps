package at.sunilson.stylishmaps.data

import at.sunilson.stylishmaps.data.entities.SearchResponse
import at.sunilson.stylishmaps.data.entities.SearchResult
import at.sunilson.stylishmaps.data.network.RetrofitService
import at.sunilson.stylishmaps.data.network.handleRetrofitCall
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.map

interface Repository {
    suspend fun searchForLocation(query: String): SuspendableResult<List<SearchResult>, Exception>
}

internal class RepositoryImpl(private val retrofitService: RetrofitService) : Repository {
    override suspend fun searchForLocation(query: String) =
        handleRetrofitCall(retrofitService.searchLocation()).map {
            it.features.map {
                SearchResult(it.placeName)
            }
        }
}