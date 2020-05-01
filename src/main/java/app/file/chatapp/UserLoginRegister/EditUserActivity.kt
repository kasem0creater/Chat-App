package app.file.chatapp.UserLoginRegister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import app.file.chatapp.R
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import app.file.chatapp.UserLoginRegister.Usermanaget.getUserDataProfile
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_user.*

class EditUserActivity : AppCompatActivity() , getUserDataProfile.getInfoUser{

    private lateinit var userService: UserManageService
    private lateinit var userDataService: getUserDataProfile
    private lateinit var shared: SharedPreferenceManager
    private val IMAGE_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.EditTheme)
        setContentView(R.layout.activity_edit_user)

        //get instance user service
        userService =
            UserManageService(
                this@EditUserActivity
            )
        shared =
            SharedPreferenceManager(this@EditUserActivity)
        userDataService =
            getUserDataProfile(
                this@EditUserActivity,
                this
            )

        userDataService.getUserinfo(userService.getCurrentUser()?.uid.orEmpty())


        //text data usre
        setDataToText("", "", "", "")



        //user name setting
        txt_user_edit.setText(shared.getUserName())
        //profile_image.setImageURI(Uri.parse(shared.getImageUri()))


        // update imager and add image profile
        updateImageProfile()

        //update user info data
        updateUserData()

        // card background
        settingBackground()
    }


    //card abce ground setting
    private fun settingBackground()
    {
        card_background_edit.setBackgroundResource(R.drawable.background_corner_edit)
    }


    // update profile image
    private fun updateImageProfile()
    {
        profile_image.setOnClickListener {
           // userService.addImage()

            var intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent , IMAGE_CODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null)
        {

            val imageData = data.data
            CropImage.activity(imageData)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this@EditUserActivity)

            Log.d("image","OK")

        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            val result:CropImage.ActivityResult = CropImage.getActivityResult(data)

            shared.writeImageURI(result.uri)
            userService.addImage(result.uri)
            profile_image.setImageURI(Uri.parse(shared.getImageUri()))
        }
        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
        {
            Log.d("data","Error")
        }
        else
        {
            Log.d("image","Error")
        }
    }

    private val USER_NAME="USER_NAME"
    private val LAST_NAME="LAST_NAME"
    private val NICK_NAME="NICK_NAME"
    private val IMAGE_URI="IMAGE_URI"



    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        profile_image.setImageURI(Uri.parse(shared.getImageUri()))


    }


    /*
    save user info
     - name
     - last anem
     - nick name

     */
    private fun updateUserData()
    {
        btn_update_data_edit.setOnClickListener {
            userService.updateUserInfo(txt_user_edit.text.toString(),
                txt_lastName_edit.text.toString(),
                txt_nickName_edit.text.toString())
        }
    }

    override fun Data(userName: String, lastName: String, nickName: String, imageURL: String) {

        setDataToText(userName , lastName , nickName , imageURL)
    }

    //set data user into to text view
    private fun setDataToText(
        userName: String,
        lastName: String,
        nickName: String,
        imageURL: String
    )
    {
        txt_nickName_edit.setText(userName)
        txt_lastName_edit.setText(lastName)
        txt_nickName_edit.setText(nickName)

        Glide.with(this).load(imageURL).into(profile_image)
    }
}
