package maasrahman.com.testsocketio.network

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(
    context: Context
) : ConnectivityInterceptor {
    val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isOnlie()){
            throw ConnetionException()
        }
        return chain.proceed(chain.request())
    }

    fun isOnlie() : Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return info != null && info.isConnected
    }
}