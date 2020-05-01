package app.file.chatapp.Recyclemanaget.message

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import app.file.chatapp.Entity.Messages
import app.file.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessageAdater(val item:MutableList<Messages>, val context:Context, friendId:String) : RecyclerView.Adapter<MessageViewHolder>()
{

    //setting firebase db
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mUser:DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_message_layout , parent ,false) as View

        //get instance firebase authentication
        mAuth = FirebaseAuth.getInstance()

        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        //notifyDataSetChanged()

        val messageSenderId = mAuth.currentUser?.uid.orEmpty()
        //
        mUser = FirebaseDatabase.getInstance().reference.child("Messages")
        mUser

        val messageModel: Messages = item.get(position)
        val fromUserId = messageModel.from
        val messageType = messageModel.type

        //
        mUser = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        mUser.child(fromUserId).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(dataSnapshot: DatabaseError) {
                Log.w("Error read message",dataSnapshot.toException().toString())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.hasChild("Image"))
                    {
                        var image= dataSnapshot.child("Image").value.toString()
                        Log.i("Data images","Data")
                        Log.i("Data images",image)
                        //load image
                        //setting image user receiver
                        Glide.with(context.applicationContext).load(image).error(R.drawable.icons8_user_account_50px)
                            .into(holder.imageUser)
                    }
                    else
                    {
                        Log.i("Data images","null")
                    }
            }
        })

        holder.txtSendMessage.visibility = View.GONE
        holder.txtFriendMessage.visibility = View.GONE
        holder.imageUser.visibility = View.GONE
        holder.imageSender.visibility = View.GONE
        holder.imageFriend.visibility = View.GONE

        //check message type
        if(messageType.equals("text"))
        {
            //
            if(fromUserId.equals(messageSenderId))
            {
                holder.txtSendMessage.visibility = View.VISIBLE

                holder.txtSendMessage.setBackgroundResource(R.drawable.sender_message_layout)
                holder.txtSendMessage.setTextColor(Color.WHITE)
                holder.txtSendMessage.text = messageModel.message

            }
            else
            {
                holder.txtSendMessage.visibility = View.GONE
                holder.txtFriendMessage.visibility = View.VISIBLE
                holder.imageUser.visibility = View.VISIBLE


                //
                holder.txtFriendMessage.setBackgroundResource(R.drawable.receiver_message_layout)
                holder.txtFriendMessage.setTextColor(Color.BLACK)
                holder.txtFriendMessage.text = messageModel.message
            }
        }
        if(messageType.equals("image"))
        {
            if(fromUserId.equals(messageSenderId))
            {

                holder.imageSender.visibility = View.VISIBLE
                holder.imageFriend.visibility = View.GONE

                //image
                Glide.with(context.applicationContext).load(messageModel.message)
                    .error(R.drawable.icons8_user_account_50px).into(holder.imageSender)
            }
            else
            {
                holder.imageSender.visibility = View.GONE
                // holder.imageUser.visibility = View.VISIBLE
                holder.imageFriend.visibility = View.VISIBLE

                //image
                Glide.with(context.applicationContext).load(messageModel.message)
                    .error(R.drawable.icons8_user_account_50px).into(holder.imageFriend)
            }
        }
    }
}