package app.file.chatapp.Chat.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import app.file.chatapp.Chat.ChatManagement
import app.file.chatapp.R
import kotlinx.android.synthetic.main.activity_group_message.*

class GroupMessageActivity : AppCompatActivity() {

    private lateinit var group_name:String

    private lateinit var chatService: ChatManagement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_message)

        //set instance chat service class
        chatService =
            ChatManagement(this@GroupMessageActivity)


        //get group name
        group_name = intent.getStringExtra("GROUP_NAME_MESSAGE")
        //toolBar setting
        appBarSetting()


        //user send message
        sendMessage()

    }

    //setting app bar title name
    private fun appBarSetting()
    {
        var tooBar = findViewById<Toolbar>(R.id.app_bar_group)

        tooBar.title = group_name

        setSupportActionBar(tooBar)

        // click icon arrow back activity
        tooBar.setOnClickListener {
         finish()
        }
    }

    //send message to group
    fun sendMessage()
    {
        btn_sendMessage.setOnClickListener {
            chatService.groupSendMag(group_name,txt_messages.text.toString())

            txt_messages.setText("")
        }
    }
}
