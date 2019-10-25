package at.sunilson.stylishmaps.data.network

import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call

suspend fun <T : Any> handleRetrofitCall(call: Call<T>) = withContext(Dispatchers.IO) {
    SuspendableResult.of<T, Exception> {
        val result = call.execute()

        if (!result.isSuccessful) {
            throw NetworkException(result.code(), result.errorBody()?.string())
        }

        result.body()!!
    }
}

suspend fun handleEmptyRetrofitCall(call: Call<Void>) = withContext(Dispatchers.IO) {
    SuspendableResult.of<Unit, Exception> {
        val result = call.execute()
        if (!result.isSuccessful) {
            throw NetworkException(result.code(), result.errorBody()?.string())
        }
    }
}

data class NetworkException(val code: Int, val body: String?) : Exception()
