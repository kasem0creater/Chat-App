package app.file.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import app.file.chatapp.UserLoginRegister.Usermanaget.getUserDataProfile
import app.file.chatapp.notifications.retrofitCallNotify.BaseUrl
import app.file.chatapp.notifications.retrofitCallNotify.NotifySendInterface
import app.file.chatapp.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() , getUserDataProfile.getInfoUser{

    private lateinit var friend_user_id:String
    private lateinit var senderUserID:String
    private var currenc_State:String = "new"
    private lateinit var userService: getUserDataProfile
    private lateinit var userManament: UserManageService
    private val mDatabase = FirebaseDatabase.getInstance()
    private lateinit var mRef:DatabaseReference
    private lateinit var mFriend:DatabaseReference
    private lateinit var mContacts:DatabaseReference
    private lateinit var mNotify:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        //uid in account for user
        friend_user_id = intent.getStringExtra("VISIT_USER_ID").toString()

        //new instance user profile class
        userService =
            getUserDataProfile(
                this,
                this
            )
        userManament =
            UserManageService(
                this
            )

        //user Id in current state
        senderUserID = userManament.getCurrentUser()?.uid.toString()

            //Toast.makeText(this,friend_user_id,Toast.LENGTH_LONG).show()

        //button click on send message request
       manageRequests()
    }

    override fun onStart() {
        super.onStart()

        //get user info from firebase db
        //and call interface Data
        userService.getUserinfo(friend_user_id)

    }
    override fun Data(userName: String, lastName: String, nickName: String, imageURL: String) {
        //
        //setting user name nick name and user image profile
        txt_userName.text = userName.orEmpty()
        txt_nickName.text = nickName.orEmpty()
        //setting image profile
        Glide.with(this).load(imageURL).error(R.drawable.icons8_user_account_50px).into(image_user_profile)
    }

    /*
    send request to friend
     */
    private fun manageRequests()
    {
        //check request
        checkStateRequests()


        // userID == userID -> enable = false : true
        if(!senderUserID.equals(friend_user_id))
        {
            btn_add_friend.setOnClickListener {
                btn_add_friend.isEnabled = false
                //call method send request and send parametor current user ID
                //and friend user ID
               if(currenc_State.equals("new"))
               {
                   mRef = mDatabase.reference.child("RequestsFriend")
                   //save senderID is Current User and friendID is other users to db
                   mRef.child(senderUserID).child(friend_user_id).child("RequestType").setValue("Send").addOnCompleteListener {
                       if(it.isSuccessful)
                       {
                       }
                       else
                       {
                           Log.e("requests","Request to Friend :"+it.exception.toString())
                       }
                   }

                   //
                   mRef.child(friend_user_id).child(senderUserID).child("RequestType").setValue("Request").addOnCompleteListener {
                       if(it.isSuccessful)
                       {
                           //notify system
                           notificationRequest()

                           //send notify
                           sendNotify(friend_user_id , "Request Friend" , "wants to be your friend")
                       }
                   }
               }
               //check current state
                // id current -> send
                //if to work cancel Request
                //.change state then send request successful..
                if(currenc_State.equals("RequestSend"))
               {
                   cancelRequestFriend()
                   //if cancel success will change state to new
               }
                //state request if check in method
                //checkState
                //if current user send request to friend
                // state -> request send
                //friend state -> request
                 if(currenc_State.equals("Request"))
               {
                   //call accept friend
                   acceptFriend()
               }
                // if
                if(currenc_State.equals("friend"))
               {
                   removeFriend()
               }
            }
        }
        else
        {
            btn_add_friend.visibility = View.INVISIBLE
        }
    }




    /*
    create notify and send notify to user and friend
    use firebase message
     */
    private fun notificationRequest() {
        //
        var chatNotifyRequest:MutableMap<String,String> = HashMap<String , String>()
        chatNotifyRequest.put("from",senderUserID)
        chatNotifyRequest.put("type","Request")

        mNotify = FirebaseDatabase.getInstance().reference.child("Notifications")
        mNotify.child(friend_user_id).push().setValue(chatNotifyRequest).addOnCompleteListener {
            if(it.isSuccessful)
            {

                //changeButtonText if state -> false is save data successful..
                //and true is save error
                btn_add_friend.text = "Cancel Request Friend"
                currenc_State = ""
                currenc_State = "RequestSend"
                Log.i("requests","Request to Friend Successful..."+currenc_State)

            }
        }
    }

    private fun removeFriend() {
        mContacts = mDatabase.reference.child("Contacts")
        //remove current user from friend
        val query = mContacts.child(senderUserID).child(friend_user_id)
            .removeValue().addOnCompleteListener {
                if(it.isSuccessful)
                {
                    // remove friend
                }
            }
        //remove friend
        mContacts.child(friend_user_id).child(senderUserID).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    //remove request
                    cancelRequestFriend()
                }
            }
    }

    private  fun checkStateRequests()
    {
        mFriend = mDatabase.reference.child("RequestsFriend")
        mFriend.child(senderUserID).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("Error get Request", dataSnapshot.toException().toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //Log.d("work","Event Check")
                    if(dataSnapshot.hasChild(friend_user_id))
                    {
                        var request_Type = dataSnapshot.child(friend_user_id).child("RequestType").value.toString()
                        Log.i("work",request_Type)

                        /*
                        check state from list friend
                           if is send give call
                           cancelRequest
                         */
                        if(request_Type.equals("Send"))
                        {
                            currenc_State = "RequestSend"
                            btn_add_friend.text = "Cancel Request Friend"
                        }


                        /*
                        else send but is Request give
                        if request and check friend contacts
                        if there is data in friend contact
                        give state = friend call method remove friend
                        else state = request call method accept friend
                         */
                        else if(request_Type.equals("Request"))
                        {
                            //give call method accept friend
                            Log.i("RequestType",request_Type)
                            currenc_State = "Request"
                            btn_add_friend.text = "Accept Friend"
                            btn_accept_friend.visibility = View.VISIBLE
                            btn_accept_friend.isEnabled = true

                            //on click accept friends
                            btn_accept_friend.setOnClickListener {
                                cancelRequestFriend()
                            }

                    }
                }

                //check friend to current user
                Log.d("accept F","")

                //check contact friend list
                mContacts = mDatabase.reference.child("Contacts")
                val query = mContacts.orderByKey().equalTo(senderUserID)
                query.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children)
                        {
                            //check if state is request and
                            // if contact there is data
                                //contact data
                                if(data.hasChild(friend_user_id))
                                {
                                    currenc_State = "friend"
                                    btn_add_friend.text = "Remove Friend"
                                    btn_accept_friend.visibility = View.INVISIBLE
                                }
                                //if contact not have data
                                //current user and id friend not is friend
                                else
                                {


                                }
                        }
                    }

                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        Log.w("Error get state request", dataSnapshot.toException().toString())
                    }
                })
            }
        })
    }
    /*
    cancel request to friend
     */
    private fun cancelRequestFriend()
    {
        //mRef = null
        mRef = mDatabase.reference.child("RequestsFriend")
        mRef.child(senderUserID).child(friend_user_id).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {

            }
        }
        mRef.child(friend_user_id).child(senderUserID).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {
                btn_add_friend.isEnabled = true
                currenc_State = "new"
                btn_add_friend.text = "Request Friend"
                Log.i("cancel Request","Successful...")

                btn_accept_friend.visibility = View.INVISIBLE
                btn_accept_friend.isEnabled = false
            }
        }
    }

    /*
    accept request friend
    then onClick button accept
    will to send request accept
    to user that send request
     */
    private fun acceptFriend()
    {
        mContacts = mDatabase.reference.child("Contacts")
        //owner send request and user can get
        mContacts.child(senderUserID).child(friend_user_id).child("Contacts").setValue("Saved").addOnCompleteListener {
                if(it.isSuccessful)
                {
                    mContacts.child(friend_user_id).child(senderUserID).child("Contacts").setValue("saved").addOnCompleteListener {
                            if (it.isSuccessful)
                            {
                                //using when user accept request friend
                                mContacts.child(friend_user_id).child(senderUserID).child("Contacts").setValue("saved").addOnCompleteListener {
                                    if (it.isSuccessful)
                                    {
                                        //
                                        btn_add_friend.isEnabled = true
                                        currenc_State = "friend"
                                        btn_add_friend.text = "Remove Friend"
                                        btn_accept_friend.visibility = View.INVISIBLE

                                        //send notify
                                        sendNotify(friend_user_id ,"Request Friend","your there is add friend")
                                    }
                                }
                            }
                        }
                }
            }
    }

    //
    //using when user accept request friend
    //remove request all user that request and friend
    private fun removeRequest() {
        mRef = mDatabase.reference.child("RequestsFriend")
        mRef.child(senderUserID).child(friend_user_id).removeValue().addOnCompleteListener {
                if(it.isSuccessful)
                {
                    //todo
                    mRef.child(friend_user_id).child(senderUserID).removeValue().addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            btn_add_friend.isEnabled = true
                            currenc_State = "friend"
                            btn_add_friend.text = "Remove Friend"
                            btn_accept_friend.visibility = View.INVISIBLE
                        }
                    }
                }
            }
    }

    /*
   setting notify
   send request notify to friend
    */
    private fun sendNotify(friendId:String , title:String , message:String)
    {
        // find device of user
        mRef = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        mRef.child(friendId).addValueEventListener(object :ValueEventListener{
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
                    mapBody.put("body" ,"${name} ${message}")
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
                                Log.i("send Notify","Successful..."+response.body().toString()+token)
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

}
