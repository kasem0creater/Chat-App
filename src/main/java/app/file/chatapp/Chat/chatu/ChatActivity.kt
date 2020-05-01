package app.file.chatapp.Chat.chatu

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import app.file.chatapp.Entity.Messages
import app.file.chatapp.R
import app.file.chatapp.Recyclemanaget.message.MessageAdater
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import app.file.chatapp.notifications.retrofitCallNotify.BaseUrl
import app.file.chatapp.notifications.retrofitCallNotify.NotifySendInterface
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.HashMap


class ChatActivity : AppCompatActivity() {

    private lateinit var userName:TextView
    private lateinit var imageUser:CircleImageView
    private lateinit var userService: UserManageService
    private var uid:String = ""
    private var image:String =  ""
    private var nickName:String =""
    private lateinit var mRef:DatabaseReference
    private lateinit var mUser:DatabaseReference
    //adapter
    private lateinit var adapter: MessageAdater
    private var messageList = mutableListOf<Messages>()
    private lateinit var shared: SharedPreferenceManager
    private val imageCode:String = "01"
    private var myUri = ""
    private  var count:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //setting user service management
        userService =
            UserManageService(
                this@ChatActivity
            )

        //share reF
        shared =
            SharedPreferenceManager(this)



        var user:String = ""
        user =  intent.getStringExtra("UserName")
        uid = intent.getStringExtra("UserID")
        image = intent.getStringExtra("UserImage")
        getNickName()

        settingCustomAppBar()

        //setting data Ref
        mRef = FirebaseDatabase.getInstance().reference

        //
        userName.text = user
        if(!image.isNullOrEmpty())
        {
            Glide.with(this.applicationContext).load(image).error(R.drawable.icons8_user_account_50px).into(imageUser)
        }

        //method send message
       btn_sendMessage.setOnClickListener {
           sendMessage()
       }

        //method send Image on message
        btn_send_image.setOnClickListener {
            sendImage()
        }

        getMessages()

    }

    private fun settingEditText()
    {
        txt_messages.height
    }


    private fun settingCustomAppBar() {

        //setting app bar
        var toolbarChat:Toolbar = findViewById(R.id.app_bar_message_of_user) as Toolbar
        setSupportActionBar(toolbarChat)

        var actionBarChat = supportActionBar

        actionBarChat!!.setDisplayHomeAsUpEnabled(true)
        actionBarChat.setDisplayShowCustomEnabled(true)

        //custom app bar
        var layoutInflater:LayoutInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.custom_chat_bar , null)
        actionBarChat?.customView = view

       userName = findViewById(R.id.txt_user_name_friend) as TextView
        imageUser = findViewById(R.id.image_profile_chat) as CircleImageView
    }


    /*
    get nick name current user
     */
    private fun getNickName()
    {
        mRef = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        mRef.child(userService.getCurrentUser()?.uid.orEmpty()).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val nick = p0.child("NickName").value.toString()
                nickName = nick
            }
        })
    }
    /*
    send message between current user and friend of user
     */
    private fun sendMessage()
    {
        val message = txt_messages.text.toString()
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Enter your message...",Toast.LENGTH_LONG).show()
        }
        else
        {
            //text there is a message
            //action send data message to db
            val messageSenderRef = "Messages/"+ userService.getCurrentUser()?.uid.orEmpty()+"/"+uid
            val messageFriendRef = "Messages/"+uid+"/"+userService.getCurrentUser()?.uid.orEmpty()
            
            //setting firebase dataRef
            val userManageKey  = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(userService.getCurrentUser()?.uid.orEmpty()).child(uid).push()

            var messsagePushId = userManageKey.key.toString()

            var mapMessageBody = HashMap<String, Any>()

            mapMessageBody.put("message",message)
            mapMessageBody.put("type","text")
            mapMessageBody.put("from",userService.getCurrentUser()?.uid.orEmpty())

            var mapMessageDetial = HashMap<String,Any>()
            mapMessageDetial.put(messageSenderRef +"/"+messsagePushId, mapMessageBody)
            mapMessageDetial.put(messageFriendRef+"/"+messsagePushId , mapMessageBody)

            mUser = FirebaseDatabase.getInstance().reference
            mUser.updateChildren(mapMessageDetial).addOnCompleteListener {
                if(it.isSuccessful)
                {
                    //Toast.makeText(this,"Successful...",Toast.LENGTH_LONG).show()
                    sendNotify(uid,"Message","${nickName}: ${message}")
                    //messageList.clear()
                }
                else
                {
                    Toast.makeText(this,"Error...",Toast.LENGTH_LONG).show()
                }
                txt_messages.setText("")
            }
        }
    }

    //get message
    private fun getMessages()
    {
        //get data message from db
        mRef.child("Messages").child(userService.getCurrentUser()?.uid.toString()).child(uid).addChildEventListener(object :ChildEventListener{
            override fun onCancelled(dataSnapshot: DatabaseError) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val message: Messages? = dataSnapshot.getValue(
                    Messages::class.java)
                if (message != null) {
                    messageList.add(message)
                    //messageList.clear()
                    // Log.w("Error Message","null")
                }
                adapter.notifyDataSetChanged()
                recycle_message_list_of_user.smoothScrollToPosition(recycle_message_list_of_user.adapter!!.itemCount)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }
        })

        //setting adapter recycle view
        adapter = MessageAdater(
            messageList,
            this,
            uid
        )
        recycle_message_list_of_user.adapter = adapter
        recycle_message_list_of_user.hasFixedSize()
        recycle_message_list_of_user.layoutManager = LinearLayoutManager(this)
    }

    /*
   setting notify
   send request notify to friend
    */
    private fun sendNotify(friendId:String , title:String , message:String)
    {
        // find device of user
        mUser = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        mUser.child(friendId).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("get Device Token", dataSnapshot.toException().toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.hasChild("DeviceToken"))
                {
                    val token = dataSnapshot.child("DeviceToken").value.toString()
                    val name = dataSnapshot.child("NickName").value.toString()

                    // get instance retrofit
                    //var notifyCall = BaseUrl().getAPIClient()!!.create(NotifySendInterface::class.java)

                    //map body
                    val mapBody:MutableMap<String , Any> = hashMapOf()
                    mapBody.put("title" ,"${title}")
                    mapBody.put("body" ,"${message}")
                    mapBody.put("sound","bootanim")
                    mapBody.put("icon","chat_menu")
                    //map to
                    val mapRoot:MutableMap<String,Any> = hashMapOf()
                    mapRoot.put("to",token)
                    // mapRoot.put("priority","high")
                    mapRoot.put("notification" ,mapBody)

                    // build retrofit client and interface method
                    val api = BaseUrl()
                        .getAPIClient().create(NotifySendInterface::class.java)
                    var call:Call<ResponseBody> = api.sendNotify(mapRoot)

                    //asynchronous
                    call.enqueue(object :Callback<ResponseBody>{
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.w("Error send Notify",t.message.toString())
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if(response.isSuccessful)
                            {
                                Log.i("send Notify","Successful..."+response.body().toString())
                            }
                            else
                            {
                                Log.w("Error send Notify",response.raw().toString())
                            }
                        }
                    })


                    /*
             ****************       working code  ******
                       //map body
        val mapBody:MutableMap<String , Any> = hashMapOf()
        mapBody.put("title" ,"Request")
        mapBody.put("body" ,"Friends")
        mapBody.put("sound","bootanim")
        mapBody.put("icon","chat_menu")
        //map to
        val mapRoot:MutableMap<String,Any> = hashMapOf()
        mapRoot.put("to","dltix_bZaRw:APA91bFvar3-3vYXEFfLnJAlHqx97a0z0wp3kK-HtqDEFNOHJRqVE-uIg0WCrsIe8SZ2WMWc3_P1TqdIN6Vu7sRC08YZJ0eFQxAI7-pvSw-HEGHmWirejy1XKcSwFvexWNTsq5FJUQiv")
        mapRoot.put("notification" ,mapBody)

        //convert map to json data
        val gson = Gson()
        val stringJson = gson.toJson(mapRoot)
        //val jsonData = gson.fromJson(stringJson , St)

        var baseUrl = BaseUrl()
        val model = RootModel("dltix_bZaRw:APA91bFvar3-3vYXEFfLnJAlHqx97a0z0wp3kK-HtqDEFNOHJRqVE-uIg0WCrsIe8SZ2WMWc3_P1TqdIN6Vu7sRC08YZJ0eFQxAI7-pvSw-HEGHmWirejy1XKcSwFvexWNTsq5FJUQiv", Notification("requests","Friend"))
        var api = baseUrl.getAPIClient().create(NotifySendInterface::class.java) as NotifySendInterface
        var call:Call<ResponseBody> = api.sendNotify(mapRoot)

        call.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("NN", "Error con")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful)
                {
                    Log.i("Token res", response.body().toString())
                }
                else
                {
                    Log.i("Error res", response.raw().toString())
                }
            }
        })
                     */


                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        image = ""
    }

    private fun sendImage()
    {
        var intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent ,"select your images") ,438 )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 438 && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            val uriImage:Uri = data.data!!

            val sReF:StorageReference = FirebaseStorage.getInstance().reference.child("Image Message")

            //text there is a message
            //action send data message to db
            val messageSenderRef = "Messages/"+ userService.getCurrentUser()?.uid.orEmpty()+"/"+uid
            val messageFriendRef = "Messages/"+uid+"/"+userService.getCurrentUser()?.uid.orEmpty()

            //setting firebase dataRef
            val userManageKey  = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(userService.getCurrentUser()?.uid.orEmpty()).child(uid).push()
            mUser = FirebaseDatabase.getInstance().reference
            var messsagePushId = userManageKey.key.toString()

            val filePath = sReF.child(messsagePushId + "."+"jpg")

            val dialog = ProgressDialog(this)
            dialog.setTitle("Pleas wait")
            dialog.setMessage("Upload Images")
            //dialog.show()

             filePath.putFile(uriImage!!).addOnCompleteListener {
                 if(it.isSuccessful)
                 {
                     filePath.downloadUrl.addOnCompleteListener {
                         if(it.isSuccessful)
                         {
                             //dialog.dismiss()

                             myUri = it.result.toString()

                             var mapMessageBody = HashMap<String, Any>()

                             mapMessageBody.put("message",myUri)
                             mapMessageBody.put("type","image")
                             mapMessageBody.put("from",userService.getCurrentUser()?.uid.orEmpty())

                             var mapMessageDetial = HashMap<String,Any>()
                             mapMessageDetial.put(messageSenderRef +"/"+messsagePushId, mapMessageBody)
                             mapMessageDetial.put(messageFriendRef+"/"+messsagePushId , mapMessageBody)

                             Log.i("Upload image message",myUri)

                             mRef = FirebaseDatabase.getInstance().reference
                             mRef.updateChildren(mapMessageDetial).addOnCompleteListener {
                                 if(it.isSuccessful)
                                 {
                                     Log.i("send image","Successful..")
                                     sendNotify(uid,"Message","${nickName} Send Images To You")
                                    // messageList.clear()
                                 }
                             }
                         }
                     }
                 }
             }
        }
    }
}
