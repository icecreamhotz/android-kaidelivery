package app.icecreamhot.kaidelivery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import app.icecreamhot.kaidelivery.model.User
import app.icecreamhot.kaidelivery.network.UserAPI
import app.icecreamhot.kaidelivery.utils.MY_PREFS
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val userAPI by lazy {
        UserAPI.create()
    }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val setOnClickSubmit = View.OnClickListener { _ ->
            saveUserDataToDB()
        }
        btnSubmit.setOnClickListener(setOnClickSubmit)
    }

    private fun saveUserDataToDB() {
        val username = edtUsername.text.toString()
        val password = edtPassword.text.toString()
        val name = edtName.text.toString()
        val lastname = edtLastname.text.toString()
        val email = edtEmail.text.toString()
        val telephone = edtTelephone.text.toString()
        val address = edtAddress.text.toString()

        disposable = userAPI.signup(username, password, name, lastname, email, telephone, address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                        result -> loginSuccess(result)
                },
                {
                        e -> Log.d("err", e.message)
                }
            )
    }

    private fun loginSuccess(user: User) {
        val shared = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("token", user.token)
        editor.commit()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

//    fun String.isValidEmail() : Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
//
//    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
//        this.addTextChangedListener(object: TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                afterTextChanged.invoke(s.toString())
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
//        })
//    }
//
//    fun EditText.validate(validator: (String) -> Boolean, message: String) {
//        this.afterTextChanged {
//            this.error = if (validator(it)) null else message
//        }
//        this.error = if (validator(this.text.toString())) null else message
//    }

}
