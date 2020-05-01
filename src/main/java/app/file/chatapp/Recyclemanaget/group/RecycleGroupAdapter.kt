package app.file.chatapp.Recyclemanaget.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.file.chatapp.R

class RecycleGroupAdapter(item:MutableList<String> , interfaceClick: GroupItemClick):RecyclerView.Adapter<GroupViewHolder>() {

    val itemGroup = item
    private  val clickItemListen: GroupItemClick = interfaceClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_group_detial , parent ,false) as View

        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemGroup.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
       holder.txt_groupName.text = itemGroup.get(position)

        //click
       holder.itemView.setOnClickListener {
           clickItemListen.ietmClick(position , itemGroup.get(position))
       }
    }

}