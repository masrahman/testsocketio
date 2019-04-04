package maasrahman.com.testsocketio

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import maasrahman.com.testsocketio.network.ApiService
import maasrahman.com.testsocketio.network.ConnectivityInterceptorImpl
import maasrahman.com.testsocketio.network.response.DataModel

class LoginActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        apiService = ApiService.invoke(this@LoginActivity, ConnectivityInterceptorImpl(this@LoginActivity))
        button.setOnClickListener {
            if(!TextUtils.isEmpty(etUsername.text.toString())){
                progressBar.visibility = View.VISIBLE
                var model = DataModel(null, null, etUsername.text.toString(), null)
                GlobalScope.launch(Dispatchers.Main) {
                    val result = apiService.getByName(model).await()
                    if(result.data == null){
                        val cekInsert = apiService.addUser(model).await()
                        if(cekInsert.data == null){
                            Toast.makeText(this@LoginActivity, "Error Insert User", Toast.LENGTH_SHORT).show()
                        }else{
                            toMain(cekInsert.data)
                        }
                    }else{
                        toMain(result.data)
                    }
                }
            }
        }
    }

    private fun toMain(model: DataModel){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("usermodel", model)
        startActivity(intent)
        finish()
    }
}