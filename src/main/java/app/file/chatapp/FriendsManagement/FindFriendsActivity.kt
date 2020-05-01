package app.file.chatapp.FriendsManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import app.file.chatapp.Entity.Contacts
import app.file.chatapp.R
import app.file.chatapp.Recyclemanaget.firend.FindFirendHolder
import app.file.chatapp.Recyclemanaget.firend.FindFirendListen
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import app.file.chatapp.UserProfileActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_find_friends.*

class FindFriendsActivity : AppCompatActivity(),
    FindFirendListen {

    private lateinit var userRef:DatabaseReference
    private lateinit var layout:RecyclerView.LayoutManager
    private lateinit var userService: UserManageService
    private lateinit var shared: SharedPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friends)

        //app bar
        settingAppBar()

        //set instance firebase database
        userRef = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")

        //setting layout for recycle view
        layout = LinearLayoutManager(this)

        userService =
            UserManageService(
                this
            )
        shared =
            SharedPreferenceManager(this)
    }

    /*
    setting too bar add search box
    seting
      -title
      -search attribute
     */
    private fun settingAppBar()
    {
        var tooBar = findViewById<Toolbar>(R.id.appBar_findFriends_list)

        //set title too bar
        tooBar.title = "find friends"
        setSupportActionBar(tooBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStart() {
        super.onStart()


        val option = FirebaseRecyclerOptions.Builder<Contacts>().setQuery(userRef,object :SnapshotParser<Contacts>{

            override fun parseSnapshot(snapshot: DataSnapshot): Contacts {
                if(snapshot.hasChild("Email"))
                {
                    val name = snapshot.child("UserName").value.toString()
                    val nickName = snapshot.child("NickName").value.toString()
                    val image = snapshot.child("Image").value.toString()
                    val uid = snapshot.child("Uid").value.toString()

                    return Contacts(
                        "${name}",
                        "${nickName}",
                        "${image}",
                        "$uid"
                    )
                }
                return Contacts()
            }
        }).build()

        val adapter = object :FirebaseRecyclerAdapter<Contacts, FindFirendHolder>(option){
            override fun onBindViewHolder(holder: FindFirendHolder, position: Int, data: Contacts) {
                val name = data.UserName
                val nickName = data.NickName

                holder.txt_Name.text = name
                holder.txt_nickName.text = nickName

                Glide.with(this@FindFriendsActivity).load(data.Image).error(R.drawable.icons8_user_account_50px).into(holder.image_user)

                holder.itemView.setOnClickListener {
                    onClick(position ,data)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindFirendHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.user_detial_display_layout , parent , false)

                return FindFirendHolder(
                    view
                )
            }
        }

        recycle_friend_list.hasFixedSize()
        recycle_friend_list.layoutManager = LinearLayoutManager(this)
        recycle_friend_list.adapter = adapter
        adapter.startListening()
    }

    override fun onClick(position: Int, item: Contacts) {
        val uid = item.Uid

        val intent = Intent(this , UserProfileActivity::class.java)
        intent.putExtra("VISIT_USER_ID",uid)
        startActivity(intent)
    }

    override fun onLongClick(position: Int, item: Contacts) {
        TODO("Not yet implemented")
    }

}

