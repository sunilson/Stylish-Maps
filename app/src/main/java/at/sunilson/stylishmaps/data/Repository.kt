package at.sunilson.stylishmaps.data

import android.graphics.Bitmap
import android.net.Uri
import at.sunilson.stylishmaps.data.entities.Location
import at.sunilson.stylishmaps.data.entities.SearchResult
import at.sunilson.stylishmaps.data.network.RetrofitService
import at.sunilson.stylishmaps.data.network.handleRetrofitCall
import at.sunilson.stylishmaps.utils.BitmapUtils
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.map

interface Repository {
    suspend fun searchForLocation(query: String): SuspendableResult<List<SearchResult>, Exception>
    suspend fun cacheImage(image: Bitmap): SuspendableResult<Uri, Exception>
    suspend fun getImage(uri: Uri): SuspendableResult<Bitmap, Exception>
    suspend fun saveToGallery(uri: Uri): SuspendableResult<Unit, Exception>
}

internal class RepositoryImpl(
    private val retrofitService: RetrofitService, private val bitmapUtils: BitmapUtils
) : Repository {

    override suspend fun saveToGallery(uri: Uri) = SuspendableResult.of<Unit, Exception> {
        val result = bitmapUtils.exportBitmap(uri)
        if (!result) error("Could not save image!")
    }

    override suspend fun searchForLocation(query: String) =
        handleRetrofitCall(retrofitService.searchLocation(query)).map {
            it.features.map {
                SearchResult(it.placeName, Location(it.center[1], it.center[0]))
            }
        }

    override suspend fun getImage(uri: Uri) = SuspendableResult.of<Bitmap, Exception> {
        bitmapUtils.getBitmap(uri)
    }

    override suspend fun cacheImage(image: Bitmap) =
        SuspendableResult.of<Uri, Exception> { bitmapUtils.cacheBitmap(image) }
}