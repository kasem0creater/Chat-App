package app.file.chatapp.Recyclemanaget.firend

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class RequestFriendViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
{
    var txt_name:TextView
    var txt_nickName:TextView
    var user_image:CircleImageView
    var btn_accept: MaterialButton
    var btn_cancel: MaterialButton

    init {
        txt_name = itemView.findViewById(R.id.txt_user_profile_name_request)
        txt_nickName = itemView.findViewById(R.id.txt_user_profile_Nickname_request)
        user_image = itemView.findViewById(R.id.users_profile_images_request)
        btn_accept = itemView.findViewById(R.id.btn_accept_friend)
        btn_cancel = itemView.findViewById(R.id.btn_cancel_friend)
    }
}