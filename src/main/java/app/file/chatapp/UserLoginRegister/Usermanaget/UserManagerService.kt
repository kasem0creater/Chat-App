package app.file.chatapp.UserLoginRegister.Usermanaget

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import app.file.chatapp.Entity.UserInfo
import app.file.chatapp.MainActivity
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.EditUserActivity
import app.file.chatapp.UserLoginRegister.LoginActivity
import app.file.chatapp.UserLoginRegister.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

open class UserManageService(var context: Context )
{
    protected val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    protected var currenUser = mAuth.currentUser
    protected val mDatabase = FirebaseDatabase.getInstance()
    protected lateinit var mRef:DatabaseReference
    protected lateinit var mDataStorage:StorageReference
    protected val dataContext:Context = context
    private   var status:Boolean = true

    //
    protected val progressbar = ProgressDialog(context)
    protected lateinit var shared: SharedPreferenceManager

    /*
    get current user
     */
    fun getCurrentUser():FirebaseUser?
    {
        return currenUser
    }

    /*
    Login fun

    user firebase auth
    login email password
     */
    fun createUserAccount(userName: String, email:String ,password:String , confrimPassword:String)
    {
        if(email == null)
        {
            //
            Toast.makeText( dataContext, "Email null", Toast.LENGTH_LONG).show()
        }
        else if(password == null)
        {
            // to do
            Toast.makeText( dataContext, "password null", Toast.LENGTH_LONG).show()
        }
        else
        {
            if(confrimPassword.equals("$password"))
            {
                showDialog("Creating","Pleas wait create account....")

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        shared =
                            SharedPreferenceManager(
                                context
                            )
                        shared.writeStateCurrentUser(true)
                        shared.writeUserName(userName)
                        // add user data to db
                        //addUserInfo(userName,"","","$email")
                        mRef = mDatabase.reference.child("UserInfo")
                        var deviceToken = FirebaseInstanceId.getInstance().token
                        val data = UserInfo(
                            userName,
                            "",
                            "",
                            "$email",
                            mAuth.uid,
                            "",
                            "${deviceToken}"
                        )
                        //val key = mRef.child("UserInfo").push().key

                        mRef.child("Data").child("${mAuth.uid}").setValue(data.toMap()).addOnCompleteListener {
                            if (it.isSuccessful)
                            {
                                progressbar.dismiss()
                                Toast.makeText( dataContext, "Successful...", Toast.LENGTH_LONG).show()
                                startEditActivity()
                            }
                            else
                            {
                                progressbar.dismiss()
                                Toast.makeText( dataContext, "Successful...", Toast.LENGTH_LONG).show()
                                startEditActivity()
                            }
                        }

                    }
                    else
                    {
                        progressbar.dismiss()
                        Toast.makeText(dataContext , "Error:${it.exception.toString()}", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else
            {
                // to do
                Toast.makeText( dataContext, "Password erroe", Toast.LENGTH_LONG).show()
            }
        }
    }

    /*
    user logout
     - shared chang state
     login -> true
     o]logout -> fate
     */

    fun logOut()
    {
        updateUserStatus("offline")
        shared = SharedPreferenceManager(
            dataContext
        )
        currenUser = null
        shared.clear()
        shared.writeStateCurrentUser(false)
        mAuth.signOut()
        startLoginActivity()
    }


    //user login width email and password

    fun userLogin(email:String , password:String)
    {
        //progressbar
        showDialog("Login App","Pleas wait Login...")

        mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener {
            if(it.isSuccessful)
            {
                shared =
                    SharedPreferenceManager(
                        dataContext
                    )
                shared.writeStateCurrentUser(true)

                var deviceToken = FirebaseInstanceId.getInstance().token
                mRef = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
                mRef.child(mAuth.currentUser!!.uid.orEmpty()).child("DeviceToken").setValue(deviceToken).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        //Toast.makeText(dataContext , "Successful...", Toast.LENGTH_LONG).show()
                        progressbar.dismiss()
                        startMainActivity()
                    }
                }

            }
            else
            {
                progressbar.dismiss()
                Toast.makeText(dataContext , "Email and Password Error : ${it.exception.toString()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    //show progress dialog
    fun showDialog(title:String , content:String)
    {
        progressbar.setTitle("$title")
        progressbar.setMessage("$content")
        progressbar.show()
    }

    //
    fun startMainActivity()
    {
        dataContext.startActivity(Intent(dataContext , MainActivity::class.java))
    }
    //
    fun startLoginActivity()
    {
        dataContext.startActivity(Intent(dataContext , LoginActivity::class.java))
    }
    //
    fun startRegisterActivity()
    {
        dataContext.startActivity(Intent(dataContext , RegisterActivity::class.java))
    }
    //
    fun startEditActivity()
    {
        dataContext.startActivity(Intent(dataContext , EditUserActivity::class.java))
    }

    /*
    medthod save user info data
    to firebase db
     */
    fun updateUserInfo(userName:String, lastName:String, nickName:String)
    {
        //
        mRef = mDatabase.reference.child("UserInfo/Data")
        val data = UserInfo(
            userName,
            lastName,
            nickName,
            "${mAuth.currentUser?.email}",
            mAuth.uid,
            ""
        )
        //
        val query = mRef.orderByChild("Uid").equalTo("${mAuth.currentUser?.uid}")

        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                for(Data in datasnapshot.children)
                {
                    Data.ref.child("UserName").setValue(data.userName)
                    Data.ref.child("LastName").setValue(data.laseName)
                    Data.ref.child("NickName").setValue(data.nickName)
                    Data.ref.child("Email").setValue(data.email)
                }
                startMainActivity()
            }
        })
    }

    /*
    add image to firebase Storage
    method get image path type uri
    and save
     */
    fun addImage(resource:Uri)
    {
        mDataStorage = FirebaseStorage.getInstance().reference.child("Image Profile")
        val path = mDataStorage.child(mAuth.uid+"jpg")

        // show progress diaolog
        showDialog("Image Profile","Please wait Update...")
        //
        path.putFile(resource).addOnCompleteListener{
            if(it.isSuccessful)
            {
                var  imageURL = path.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        val imageURL = it.result.toString()
                        Log.d("imageUrL", imageURL)
                        addUserInfoImage(imageURL)
                    }
                }


            }
        }
    }

    /*
    user add image
    todo keep image Url
    and save to firebas
    where user collection data
     */
    private fun addUserInfoImage(imageURL:String)
    {
        mRef = mDatabase.reference.child("UserInfo/Data")
        val dataUser = UserInfo(
            "",
            "",
            "",
            "${currenUser?.email.toString()}",
            mAuth.uid,
            "$imageURL"
        )
        val query = mRef.orderByChild("Uid").equalTo("${mAuth.currentUser?.uid}")

        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                for(data in datasnapshot.children)
                {
                    data.ref.child("Image").setValue(dataUser.imageURl)
                }
                progressbar.dismiss()
            }
        })
    }

    /*
  write user state online and offline
   */
    private fun updateUserStatus(status:String)
    {
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("mm dd yyyy")
        val currentTime = SimpleDateFormat("hh:mm a")

        val date = currentDate.format(calendar.time)
        val time = currentTime.format(calendar.time)

        val mapOnline:MutableMap<String,Any> = hashMapOf()
        mapOnline.put("time",time)
        mapOnline.put("date",date)
        mapOnline.put("status","${status}")

        mRef = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        mRef.child(mAuth.uid.orEmpty()).child("UserStatus").
        updateChildren(mapOnline)

    }


    fun forgetPassword()
    {

    }
}

class getUserDataProfile(context: Context , getinfo: getInfoUser) : UserManageService(context)
{

    private val getInfo = getinfo

    /*
   read user info from firebase db
   read to bottom sheet profile
   -name
   -nick name
   -image profile
    */
    fun getUserinfo(uid:String)
    {
        mRef = mDatabase.reference.child("UserInfo/Data")
        val query =  mRef.orderByChild("Uid").equalTo("${uid}")
        //
        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("Error","Connect Error")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var name = ""
                var lastName = ""
                var nickName = ""
                var email = ""
                for (data in dataSnapshot.children)
                {
                    name = data.child("UserName").value.toString()
                    lastName = data.child("LastName").value.toString()
                    nickName = data.child("NickName").value.toString()
                    email = data.child("Image").value.toString()
                }

                Log.d("UserData","$name")
                /*
                todo
                save image url to collection user info
                 */
                getInfo.Data(name , lastName , nickName , email)
            }
        })

    }

    // interfac send user data
    interface getInfoUser
    {
        fun Data(userName:String , lastName: String , nickName: String , imageURL: String)
    }
}