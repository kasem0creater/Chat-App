package app.file.chatapp.Chat.group


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import app.file.chatapp.R
import app.file.chatapp.Recyclemanaget.group.GroupItemClick
import app.file.chatapp.Recyclemanaget.group.RecycleGroupAdapter
import kotlinx.android.synthetic.main.fragment_group.*

/**
 * A simple [Fragment] subclass.
 */
class GroupFragment : Fragment() ,
    GroupItemClick,
    GroupNameShow {

    private lateinit var groupService: GroupManagement
    private lateinit var groupData: ShowGroup
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: RecycleGroupAdapter


    fun newIntance():Fragment
    {
        val groupFragment = GroupFragment()
        return groupFragment
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //new intance group service
        groupService =
            GroupManagement(view.context)
        groupData = ShowGroup(view.context, this)

        //call method get group name from firebase
        groupData.getGroupName()


    }

    override fun showGroupName(groupName: MutableList<String>) {
        //setting recycle view
        settingRecycleView(groupName)
    }




    /*
    setting recycle view
    set adapter
     - layout
     */
    private fun settingRecycleView(item:MutableList<String>)
    {
        //
        layoutManager = LinearLayoutManager(context)

        /*
   get group message name
   from firebase
   and send data to recycle view
    */
       // adapter.notifyItemChanged()
        adapter = RecycleGroupAdapter(
            item,
            this
        )

        recycle_group.addItemDecoration( DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
        recycle_group.adapter = adapter
        recycle_group.layoutManager = layoutManager
        recycle_group.setHasFixedSize(true)
    }

    override fun itemLongclick(position: Int, itemName:String) {

    }

    override fun ietmClick(position: Int,itemName:String) {
       // Toast.makeText(view?.context , "$itemName", Toast.LENGTH_LONG).show()
        var intemt = Intent(context,
            GroupMessageActivity::class.java)
        intemt.putExtra("GROUP_NAME_MESSAGE",itemName)

        context?.startActivity(intemt)
    }
}
