package app.file.chatapp.CustomDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import app.file.chatapp.Chat.group.GroupManagement
import app.file.chatapp.R
import kotlinx.android.synthetic.main.dialog_new_group.*

class DialogNewGroup : DialogFragment()
{

    private lateinit var groupService: GroupManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("GROUP_NAME", txt_group_name_dialog.text.toString())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        txt_group_name_dialog.setText(savedInstanceState?.getString("GROUP_NAME").toString())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_new_group , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //new instance group service
        groupService =
            GroupManagement(view.context)

        //set dialog
        this.dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        //this close
        close()

        //new group
        newGroup()
    }

    private fun newGroup()
    {
        btn_create_group.setOnClickListener {

            groupService.createNewGroup(txt_group_name_dialog.text.toString())

            dialog.dismiss()
        }
    }

    private fun close()
    {
        btn_close_dialog.setOnClickListener {
            dialog.dismiss()
        }
    }
}