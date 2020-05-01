package app.file.chatapp.Recyclemanaget.firend

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R
import de.hdodenhof.circleimageview.CircleImageView


class FindFirendHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    var txt_Name:TextView
    var txt_nickName:TextView
    var image_user:CircleImageView

    init {
        txt_Name = itemView.findViewById(R.id.txt_user_profile_name)
        txt_nickName = itemView.findViewById(R.id.txt_user_profile_Nickname)
        image_user = itemView.findViewById(R.id.users_profile_images)
    }
}