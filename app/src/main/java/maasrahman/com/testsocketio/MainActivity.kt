package maasrahman.com.testsocketio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import maasrahman.com.testsocketio.network.ApiService
import maasrahman.com.testsocketio.network.ConnectivityInterceptorImpl
import maasrahman.com.testsocketio.network.response.DataModel
import maasrahman.com.testsocketio.network.response.SessionModel
import org.json.JSONObject

import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {
    private lateinit var socket: Socket
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private var channelId = "masarahman.com.janchuck"
    private lateinit var userModel : DataModel
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiService = ApiService.invoke(this@MainActivity, ConnectivityInterceptorImpl(this@MainActivity))
        val bundle = intent.extras
        if(bundle != null){
            userModel = bundle.getParcelable("usermodel")
        }
        try {
            socket = IO.socket(BuildConfig.BASE_URL)
            socket.on(Socket.EVENT_CONNECT) { args ->
                val model = SessionModel(socket.id())
                updateSession(model)
            }.on("maasrahman") { args ->
                val obj = args[0] as JSONObject
                val from = obj.getString("username")
                val message = obj.getString("message")
                val result = textResult.text.toString() + "\n Message : " + message
                this@MainActivity.runOnUiThread {
                    textResult.text = result
                    sendNotification(from, message)
                }
            }.on(Socket.EVENT_DISCONNECT) { println("DISCONNECT NIH") }
            socket.connect()
        } catch (e: URISyntaxException) {
            println("GAGAL EUY")
            e.printStackTrace()
        }
    }

    private fun updateSession(session: SessionModel){
        GlobalScope.launch(Dispatchers.Main) {
            val result = apiService.updateSession(userModel.id!!, session).await()
            if(result.data != null){
                Toast.makeText(this@MainActivity, "Update Session Success", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        socket.off("maasrahman")
        socket.off(Socket.EVENT_CONNECT)
        socket.off(Socket.EVENT_DISCONNECT)
        super.onDestroy()
    }

    fun sendNotification(from: String, message:String){
        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 8, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, from, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(applicationContext, channelId)
                .setContentTitle("Test Socket IO")
                .setContentText(from)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
        }else{
            builder = Notification.Builder(applicationContext)
                .setContentTitle("Test Socket IO")
                .setContentText(from)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(88, builder.build())
    }
}
