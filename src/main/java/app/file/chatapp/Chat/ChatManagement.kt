package app.file.chatapp.Chat

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatManagement(val context: Context)
{
    private val mDatabase:FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var mRef:DatabaseReference
    private val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private var userID = mAuth.currentUser?.uid


    //group chat send message
    fun groupSendMag(groupName:String,message:String)
    {


        mRef = mDatabase.reference.child("Group/Data/$groupName")
        val key = mRef.push().key.toString()

        mRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("groupSendMessage","${p0.toException()}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.ref.child(key).child(userID.orEmpty()).setValue(message)
            }
        })
    }
}