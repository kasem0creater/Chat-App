package app.file.chatapp.UserLoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.file.chatapp.R
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var userService: UserManageService
    private lateinit var shared: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set theme app
        setTheme(R.style.RegisterTheme)
        setContentView(R.layout.activity_register)

        //set instance user service
        userService =
            UserManageService(
                this@RegisterActivity
            )
        shared =
            SharedPreferenceManager(this@RegisterActivity)


        settingCardBackground()

        //create account
        newAccount()

        //back ward
        backWard()
    }

    /*
    case user on press
      back ward
      if check user state
     */
    override fun onBackPressed() {
        super.onBackPressed()
        if(shared.getStatusUser() == true)
        {
            userService.startEditActivity()
        }
    }

    /*
    setting card background color
     */
    private fun settingCardBackground()
    {
        card_container_view_register.setBackgroundResource(R.drawable.card_background_corner_register)
    }


    /*
    button click
    register
    to firebase
    auth email and pass
     */
    private fun newAccount()
    {
        btn_register.setOnClickListener {
            userService.createUserAccount(
                txt_user_register.text.toString(),
                txt_email_register.text.toString(),
                txt_password_register.text.toString(),
                txt_password_confrim_register.text.toString()
            )
        }
    }


    /*
    arrow back
    act 1. image click back
    2.button click back
     */

    private fun backWard()
    {
        image_arrow_back_register.setOnClickListener {
            finish()
        }
        //
        btn_to_login_register.setOnClickListener {
            finish()
        }
    }
}
