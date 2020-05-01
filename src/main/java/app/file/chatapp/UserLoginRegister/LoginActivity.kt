package app.file.chatapp.UserLoginRegister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.file.chatapp.R
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var userService: UserManageService
    private lateinit var shared: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //get instance user service
        userService =
            UserManageService(
                this@LoginActivity
            )
        shared =
            SharedPreferenceManager(this@LoginActivity)


        //
        settingCardBackground()

        //login
        Login()

        //register
        toRegister()
    }

    override fun onStart() {
        super.onStart()

        if(shared.getStatusUser()  == true)
        {
            userService.startMainActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        if(shared.getStatusUser()  == true)
        {
            userService.startMainActivity()
        }
    }

    /*
    setting color card title
     */
    private fun settingCardBackground()
    {
        card_tile_login.setBackgroundResource(R.drawable.card_background_title)
        //card container view
        card_container_view_login.setBackgroundResource(R.drawable.background_radius_login)
    }

    /*
    login method
     */
    private fun Login()
    {
        btn_login.setOnClickListener {
            userService.userLogin(txt_user_login.text.toString() , txt_password_login.text.toString())
        }
    }

    /*
    open register page
    button click

     */
    private fun toRegister()
    {
        btn_to_register_login.setOnClickListener {
            userService.startRegisterActivity()
        }
    }
}
