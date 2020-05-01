package app.file.chatapp.Chat.contact


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
import app.file.chatapp.Recyclemanaget.firend.DisplayFriend
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_contact.*

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment() {

    private lateinit var mContacts:DatabaseReference
    private lateinit var mUser:DatabaseReference
    private lateinit var currentUser:String
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()

    fun newIntance():Fragment
    {
        val contactFragment = ContactFragment()
        return contactFragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        currentUser = mAuth.uid.orEmpty()
        mContacts = FirebaseDatabase.getInstance().reference.child("Contacts")
        mUser = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        return view
    }

    override fun onStart() {
        super.onStart()
        // read friend display in recycle FirebaseUI

        val option = FirebaseRecyclerOptions.Builder<Contacts>()
            .setQuery(mContacts.child(currentUser),
                Contacts::class.java).build()
        
        var adapter = object :FirebaseRecyclerAdapter<Contacts, DisplayFriend>(option)
        {
            override fun onBindViewHolder(viewHolder: DisplayFriend, position: Int, model: Contacts) {
                //
                var userID = getRef(position).key
                Log.i("ID", userID)
                mUser.child(userID.orEmpty()).addValueEventListener(object :ValueEventListener{
                    override fun onCancelled(dataSnapshot: DatabaseError) {
                        Log.w("Error display Friend", dataSnapshot.toException().toString())
                    }
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.hasChild("Image"))
                            {
                                   val status = dataSnapshot.child("UserStatus").child("status").value.toString()
                                if(status.equals("online"))
                                {
                                    var profileImage = dataSnapshot.child("Image").value.toString()
                                    var name = dataSnapshot.child("UserName").value.toString()
                                    var nickName = dataSnapshot.child("NickName").value.toString()


                                    //setting parametor to view holder
                                    viewHolder.txt_Name.text = name
                                    viewHolder.txt_NickName.text = nickName
                                    viewHolder.status.visibility = View.VISIBLE
                                    //
                                    Glide.with(view!!.context.applicationContext).load(profileImage)
                                        .error(R.drawable.icons8_user_account_50px).into(viewHolder.image_User)
                                }
                                else
                                {
                                    var profileImage = dataSnapshot.child("Image").value.toString()
                                    var name = dataSnapshot.child("UserName").value.toString()
                                    var nickName = dataSnapshot.child("NickName").value.toString()


                                    //setting parametor to view holder
                                    viewHolder.txt_Name.text = name
                                    viewHolder.txt_NickName.text = nickName
                                    viewHolder.status.visibility = View.INVISIBLE
                                    //
                                    Glide.with(view!!.context.applicationContext).load(profileImage)
                                        .error(R.drawable.icons8_user_account_50px).into(viewHolder.image_User)
                                }
                            }
                        }
                })
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayFriend {
                //
                var view = LayoutInflater.from(parent.context).inflate(R.layout.user_detial_display_layout,parent,false)

                return DisplayFriend(
                    view
                )
            }

            override fun getItemCount(): Int {
                return super.getItemCount()
            }
        }
        //

        //setting recycle view display friend list
        recycle_display_friend_contact.hasFixedSize()
        recycle_display_friend_contact.layoutManager = LinearLayoutManager(view!!.context)
        recycle_display_friend_contact.adapter = adapter
        adapter.startListening()
    }


}
