package app.file.chatapp.Recyclemanaget.chat

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView

class ChatViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
{
    var txtName:TextView
    var txtNickName:TextView
    var userImage:CircleImageView
    var status:ImageView

    init {
        txtName = itemView.findViewById(R.id.txt_user_profile_name)
        txtNickName = itemView.findViewById(R.id.txt_user_profile_Nickname)
        userImage = itemView.findViewById(R.id.users_profile_images)
        status = itemView.findViewById(R.id.image_user_online_status)
    }
}