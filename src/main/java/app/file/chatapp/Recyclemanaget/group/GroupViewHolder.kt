package app.file.chatapp.Recyclemanaget.group

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R

class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    val txt_groupName:TextView

    init {
        txt_groupName = itemView.findViewById(R.id.txt_group_name_recycle)
    }
}