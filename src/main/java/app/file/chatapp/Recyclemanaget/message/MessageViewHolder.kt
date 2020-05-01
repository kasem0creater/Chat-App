package app.file.chatapp.Recyclemanaget.message

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView

class MessageViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    var txtSendMessage:TextView
    var txtFriendMessage:TextView
    var imageUser:CircleImageView
    var imageSender:ImageView
    var imageFriend:ImageView

    init {
        txtSendMessage = itemView.findViewById(R.id.txt_sender_message)
        txtFriendMessage = itemView.findViewById(R.id.txt_friend_message)
        imageUser = itemView.findViewById(R.id.image_user_message_layout)
        imageSender = itemView.findViewById(R.id.image_message_sender)
        imageFriend = itemView.findViewById(R.id.image_message_friend)
    }
}