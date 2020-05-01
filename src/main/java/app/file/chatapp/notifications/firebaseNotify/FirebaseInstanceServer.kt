package app.file.chatapp.notifications.firebaseNotify

import android.util.Log
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseInstanceServer : FirebaseInstanceIdService()
{
    val TAG = "FirebaseIDService"
    private lateinit var userService: UserManageService

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().token
        Log.i(TAG,token)

        //
        sendRegistrationToServer(token.orEmpty())
    }

    private fun sendRegistrationToServer(token: String)
    {
        userService =
            UserManageService(
                this
            )
        if(userService.getCurrentUser()?.uid != null)
        {
            var mRef:DatabaseReference = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
            val mAuth =FirebaseAuth.getInstance()
            val uid = mAuth.currentUser?.uid.orEmpty()
            mRef.child(uid).child("DeviceToken").setValue(token).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Log.i("Refresh Token ","Successful...")
                }
            }
        }
    }
}