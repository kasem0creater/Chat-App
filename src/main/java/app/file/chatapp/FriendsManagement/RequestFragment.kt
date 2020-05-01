package app.file.chatapp.FriendsManagement

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import app.file.chatapp.Entity.Contacts

import app.file.chatapp.R
import app.file.chatapp.Recyclemanaget.firend.RequestFriendViewHolder
import app.file.chatapp.notifications.retrofitCallNotify.BaseUrl
import app.file.chatapp.notifications.retrofitCallNotify.NotifySendInterface
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_request.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class RequestFragment : Fragment() {

    private lateinit var mFriend:DatabaseReference
    private lateinit var mUser:DatabaseReference
    private lateinit var mContacts:DatabaseReference
    private lateinit var mAuth:FirebaseAuth
    private var curent_User:String =""
    private var friendId:String=""

    fun newInstance():Fragment
    {
        val fragment = RequestFragment()

        return fragment
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        mAuth = FirebaseAuth.getInstance()
        curent_User = mAuth.uid.toString()
        mFriend = FirebaseDatabase.getInstance().reference.child("RequestsFriend")
        mUser = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")

        return view
    }

    override fun onStart() {
        super.onStart()

        var option = FirebaseRecyclerOptions
            .Builder<Contacts>().setQuery(mFriend.child(curent_User),
                Contacts::class.java)
            .build()

        var adapter = object :FirebaseRecyclerAdapter<Contacts, RequestFriendViewHolder>(option){
            override fun onBindViewHolder(viewHolder: RequestFriendViewHolder, position: Int, model: Contacts) {
                viewHolder.btn_accept.visibility = View.VISIBLE
                viewHolder.btn_cancel.visibility = View.VISIBLE

                val userID = getRef(position).key.orEmpty()
                Log.i("UserID",userID)
                val getTypeRef:DatabaseReference = getRef(position).child("RequestType").ref

                getTypeRef.addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(dataSnapshot: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            var type = dataSnapshot.value.toString()
                            Log.i("request List",""+type)
                            if(type.equals("Request"))
                            {
                                mUser.child(userID).addValueEventListener(object :ValueEventListener{
                                    override fun onCancelled(dataSnapshot: DatabaseError) {

                                        Log.w("Error request list",""+dataSnapshot.code.toString())

                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                           if(dataSnapshot.hasChild("Image"))
                                           {
                                               var requestUserName = dataSnapshot.child("UserName").value.toString()
                                               var requestNickName = dataSnapshot.child("NickName").value.toString()
                                               var requestImage = dataSnapshot.child("Image").value.toString()

                                               Log.i("request List",""+requestUserName)

                                               //setting view

                                               viewHolder.txt_name.text = requestUserName
                                               viewHolder.txt_nickName.text = requestNickName

                                               //
                                               Glide.with(view!!.context).load(requestImage).error(R.drawable.icons8_user_account_50px).into(viewHolder.user_image)
                                           }
                                           else
                                           {
                                               var requestUserName = dataSnapshot.child("UserName").value.toString()
                                               var requestNickName = dataSnapshot.child("NickName").value.toString()
                                             //  var requestImage = dataSnapshot.child("Image").value.toString()

                                               Log.i("request List",""+requestUserName)

                                               //setting view

                                               viewHolder.txt_name.text = requestUserName
                                               viewHolder.txt_nickName.text = requestNickName
                                           }

                                        //click item button accept and cancel
                                        viewHolder.btn_accept.setOnClickListener {
                                            //accept
                                            acceptFriend(curent_User,userID)
                                        }

                                        //cancel friend request
                                        viewHolder.btn_cancel.setOnClickListener {
                                            //cancel friend request
                                            cancelRequestFriend(curent_User ,userID)
                                        }
                                    }
                                })
                            }
                            else if (type.equals("Send"))
                            {
                                mUser.child(userID).addValueEventListener(object :ValueEventListener{
                                    override fun onCancelled(dataSnapshot: DatabaseError) {

                                        Log.w("Error request list",""+dataSnapshot.code.toString())

                                    }

                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        if(dataSnapshot.hasChild("Image"))
                                        {
                                            var requestUserName = dataSnapshot.child("UserName").value.toString()
                                            var requestNickName = dataSnapshot.child("NickName").value.toString()
                                            var requestImage = dataSnapshot.child("Image").value.toString()

                                            Log.i("request List",""+requestUserName)

                                            //setting view

                                            viewHolder.txt_name.text = requestUserName
                                            viewHolder.txt_nickName.text = requestNickName

                                            //
                                            Glide.with(view!!.context.applicationContext).load(requestImage.orEmpty()).error(R.drawable.icons8_user_account_50px).into(viewHolder.user_image)
                                        }
                                        else
                                        {
                                            var requestUserName = dataSnapshot.child("UserName").value.toString()
                                            var requestNickName = dataSnapshot.child("NickName").value.toString()
                                            //  var requestImage = dataSnapshot.child("Image").value.toString()

                                            Log.i("request List",""+requestUserName)

                                            //setting view

                                            viewHolder.txt_name.text = requestUserName
                                            viewHolder.txt_nickName.text = requestNickName
                                        }

                                        //click item button accept and cancel
                                        viewHolder.btn_accept.text = "Request Send"

                                        //cancel friend request
                                        viewHolder.btn_cancel.visibility = View.INVISIBLE
                                    }
                                })
                            }
                        }
                    }
                })

            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RequestFriendViewHolder {
                var view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.request_friend_layout,parent,false)

                return RequestFriendViewHolder(
                    view
                )
            }
        }

        //setting recycleview
        recycle_request_friends.hasFixedSize()
        recycle_request_friends.layoutManager = LinearLayoutManager(view!!.context)
        recycle_request_friends.adapter = adapter
         adapter.startListening()
    }

    /*
   accept request friend
   then onClick button accept
   will to send request accept
   to user that send request
    */
    private fun acceptFriend(currentUser:String , friendId:String)
    {
        mContacts = FirebaseDatabase.getInstance().reference.child("Contacts")
        //owner send request and user can get
        mContacts.child(currentUser).child(friendId).child("Contacts").setValue("Saved").addOnCompleteListener {
            if(it.isSuccessful)
            {
            }
        }
        //
        mContacts.child(friendId).child(currentUser).child("Contacts").setValue("saved").addOnCompleteListener {
            if (it.isSuccessful)
            {
            }
        }
        //
        mFriend.child(currentUser).child(friendId).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {
            }
        }
        mFriend.child(friendId).child(currentUser).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {
                Log.w("Request Friend accept","S")

                //send notify
                sendNotify(friendId,"Request","Accept Request Your Friend")
            }
        }

    }

    /*
   cancel request to friend
    */
    private fun cancelRequestFriend(senderUserID:String , friend_user_id:String)
    {
        //mRef = null )
        mFriend.child(senderUserID).child(friend_user_id).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {

            }
        }
        mFriend.child(friend_user_id).child(senderUserID).removeValue().addOnCompleteListener {
            if(it.isSuccessful)
            {

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
                    var call: Call<ResponseBody> = api.sendNotify(mapRoot)

                    //asynchronous
                    call.enqueue(object : Callback<ResponseBody> {
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
