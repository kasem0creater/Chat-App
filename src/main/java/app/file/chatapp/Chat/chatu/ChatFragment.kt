package app.file.chatapp.Chat.chatu


import android.content.Intent
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
import app.file.chatapp.Recyclemanaget.chat.ChatListen
import app.file.chatapp.Recyclemanaget.chat.ChatViewHolder
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() ,
    ChatListen {

    private lateinit var mChatRef:DatabaseReference
    private lateinit var mUser:DatabaseReference
    private lateinit var userService: UserManageService
    private var currentUser:String = ""
    private lateinit var shared: SharedPreferenceManager
    private var profileImage:MutableList<String> = mutableListOf("Image")


    fun newIntance():Fragment
    {
        val chatFragment = ChatFragment()
        return chatFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        //setting instances dataRef
        mChatRef = FirebaseDatabase.getInstance().reference.child("Contacts")
        mUser = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        userService =
            UserManageService(
                view.context
            )
        currentUser = userService.getCurrentUser()?.uid.orEmpty()

        userService =
            UserManageService(
                view.context
            )
        shared =
            SharedPreferenceManager(view.context)


        return view
    }

    override fun onStart() {
        super.onStart()

        //
        val option = FirebaseRecyclerOptions.Builder<Contacts>()
            .setQuery(mChatRef.child(currentUser),
                Contacts::class.java).build()

        //setting adapter recycle
        var adapter = object :FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(option)
        {
            override fun onBindViewHolder(viewHolder: ChatViewHolder, position: Int, model: Contacts) {
                var userkey = getRef(position).key.orEmpty()

                mUser.child(userkey).addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        Log.w("Error read chat",dataSnapshot.toException().toString())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                           try {

                               val status = dataSnapshot.child("UserStatus").child("status").value.toString()

                               if(status.equals("online"))
                               {
                                   Log.i("position",position.toString())
                                   //profileImage.add(position,dataSnapshot.child("Image").value.toString().orEmpty())
                                   profileImage.add( position,dataSnapshot.child("Image").value.toString().orEmpty())

                                   Glide.with(view!!.context).load(profileImage.get(position))
                                       .error(R.drawable.icons8_user_account_50px).into(viewHolder.userImage)

                                   val name = dataSnapshot.child("UserName").value.toString()
                                   val nickName = dataSnapshot.child("NickName").value.toString()
                                   val uid = dataSnapshot.child("Uid").value.toString()



                                   //setting parametor to view holder
                                   viewHolder.txtName.text = name
                                   viewHolder.txtNickName.text = nickName
                                   viewHolder.status.visibility = View.VISIBLE
                                   //

                                   //setting click open chat
                                   viewHolder.itemView.setOnClickListener {
                                       onClick(position , name ,nickName,uid,profileImage.get(position))
                                   }
                               }
                               else
                               {
                                   Log.i("position",position.toString())
                                   //profileImage.add(position,dataSnapshot.child("Image").value.toString().orEmpty())
                                   profileImage.add( position,dataSnapshot.child("Image").value.toString().orEmpty())

                                   Glide.with(view!!.context).load(profileImage.get(position))
                                       .error(R.drawable.icons8_user_account_50px).into(viewHolder.userImage)

                                   val name = dataSnapshot.child("UserName").value.toString()
                                   //val nickName = dataSnapshot.child("NickName").value.toString()
                                   val nickName = dataSnapshot.child("NickName").value.toString()
                                   val uid = dataSnapshot.child("Uid").value.toString()



                                   //setting parametor to view holder
                                   viewHolder.txtName.text = name
                                   viewHolder.txtNickName.text = nickName
                                   viewHolder.status.visibility = View.INVISIBLE
                                   //

                                   //setting click open chat
                                   viewHolder.itemView.setOnClickListener {
                                       onClick(position , name ,nickName ,uid,profileImage.get(position))
                                   }
                               }
                           }
                           catch (e:Exception)
                           {
                            Log.w("load image",e.message.orEmpty())
                           }
                        }
                    }
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.user_detial_display_layout , parent ,false)

                return ChatViewHolder(
                    view
                )
            }
        }

        //setting recycle view
        recycle_chat_list.hasFixedSize()
        recycle_chat_list.layoutManager = LinearLayoutManager(view!!.context)
        recycle_chat_list.adapter = adapter
        adapter.startListening()
    }

    /*
    open to chat Activity
    send user name and uid to activity
     */
    override fun onClick(position: Int, name:String ,nickName:String, uid:String, image:String) {
       //interface click
        //Toast.makeText(view!!.context ,""+name,Toast.LENGTH_LONG).show()

        //check online and offline status
        if(userService.getCurrentUser()?.uid != null)
        {
            shared.writeStatustOnline(true)
        }


        var intent =  Intent(view!!.context , ChatActivity::class.java)
        intent.putExtra("UserID",uid)
        intent.putExtra("UserName",name)
       // intent.putExtra("NickName",nickName)
        Log.d("image intent", image)


        intent.putExtra("UserImage",image)
            startActivity(intent)
    }

    override fun onLong(position: Int, name:String ,nickName:String, uid:String, image:String) {
        TODO("Not yet implemented")
    }
}
