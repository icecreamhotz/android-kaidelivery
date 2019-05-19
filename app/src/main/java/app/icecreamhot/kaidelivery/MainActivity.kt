package app.icecreamhot.kaidelivery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import app.icecreamhot.kaidelivery.model.User
import app.icecreamhot.kaidelivery.network.UserAPI
import app.icecreamhot.kaidelivery.ui.MainFragment
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import com.bumptech.glide.request.RequestOptions
import com.facebook.*
import com.facebook.login.LoginResult
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private val userAPI by lazy {
        UserAPI.create()
    }

    private lateinit var callbackManager: CallbackManager
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callbackManager = CallbackManager.Factory.create()
        btn_loginFacebook.setReadPermissions(listOf("public_profile", "email"))
        btn_loginFacebook.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val request = GraphRequest.newMeRequest(result?.accessToken, object: GraphRequest.GraphJSONObjectCallback {
                    override fun onCompleted(`object`: JSONObject?, response: GraphResponse?) {
                        try{
                            val provider_id = `object`?.getString("id")
                            val email = `object`?.getString("email")
                            val name = `object`?.getString("name")

                            val picture = `object`?.getJSONObject("picture")
                            val data = picture?.getJSONObject("data")
                            val url = data?.getString("url")

                            val requestOptions = RequestOptions()
                            requestOptions.dontAnimate()

                            disposable = userAPI.loginWithFacebook(provider_id, email, url, name)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe { onRetrieveStart() }
                                .doOnTerminate { onRetrieveFinish() }
                                .subscribe(
                                    {
                                        result -> loginSuccess(result)
                                    },
                                    {
                                        e -> Log.d("err", e.message)
                                        Toast.makeText(applicationContext, "Something has wrong", Toast.LENGTH_LONG).show()
                                    }
                                )

                        }catch(e:JSONException) {
                            e.printStackTrace()
                        }
                    }
                })

                val params = Bundle()
                params.putString("fields", "name,email,picture.type(large)")
                request.parameters = params
                request.executeAsync()
            }

            override fun onCancel() {
                Toast.makeText(applicationContext, "Cancel Login with facebook", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Log.d("err", error?.message)
                Toast.makeText(applicationContext, "Something has wrong", Toast.LENGTH_LONG).show()
            }

        })

        val setOnClickCommonLogin = View.OnClickListener { _ ->
            commonLogin()
        }
        btn_Login.setOnClickListener(setOnClickCommonLogin)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loginSuccess(user: User) {
        val shared = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("user_id", user.userId)
        editor.putString("token", user.token)
        editor.commit()

        val intent = Intent(this, MainFragment::class.java)
        startActivity(intent)
    }

    private fun commonLogin() {
        val username = edtUsername.text.toString()
        val password = editPassword.text.toString()

        disposable = userAPI.loginCommon(username, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveStart() }
            .doOnTerminate { onRetrieveFinish() }
            .subscribe(
                {
                        result -> loginSuccess(result.arrUserList!!)
                },
                {
                        e -> Log.d("err", e.message)
                        Toast.makeText(applicationContext, "Please c+heck your username or password", Toast.LENGTH_LONG).show()
                }
            )
    }


    private fun onRetrieveStart() {
        loading.visibility = View.VISIBLE
    }

    private fun onRetrieveFinish() {
        loading.visibility  = View.GONE
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
