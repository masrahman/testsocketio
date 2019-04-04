package maasrahman.com.testsocketio.network

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import kotlinx.coroutines.Deferred
import maasrahman.com.testsocketio.BuildConfig
import maasrahman.com.testsocketio.network.response.DataModel
import maasrahman.com.testsocketio.network.response.DataResponse
import maasrahman.com.testsocketio.network.response.SessionModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @POST("api/user")
    fun addUser(@Body model: DataModel) : Deferred<DataResponse>

    @GET("api/user/{id}")
    fun getById(@Path("id") id: String) : Deferred<DataResponse>

    @POST("api/user/{id}")
    fun updateSession(@Path("id") id: String, @Body model: SessionModel) : Deferred<DataResponse>

    @POST("api/search")
    fun getByName(@Body model: DataModel) : Deferred<DataResponse>

    companion object {
        operator fun invoke(
            context: Context, connectivityInterceptor: ConnectivityInterceptor
        ): ApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                .addInterceptor(ChuckInterceptor(context))
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}