package app.file.chatapp.Recyclemanaget.firend

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView

class DisplayFriend(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    var txt_Name:TextView
    var txt_NickName:TextView
    var image_User:CircleImageView
    var status:ImageView

    init {
        txt_Name = itemView.findViewById(R.id.txt_user_profile_name)
        txt_NickName = itemView.findViewById(R.id.txt_user_profile_Nickname)
        image_User = itemView.findViewById(R.id.users_profile_images)
        status = itemView.findViewById(R.id.image_user_online_status)
    }
}