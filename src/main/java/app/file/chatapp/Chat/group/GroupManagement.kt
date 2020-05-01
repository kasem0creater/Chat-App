package app.file.chatapp.Chat.group

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import com.google.firebase.database.*

open class GroupManagement(val context: Context)
{
    protected val mDatabase = FirebaseDatabase.getInstance()
    protected lateinit var mRef:DatabaseReference

    protected val progressbar = ProgressDialog(context)

    /*
    create new group for user
     new group message
     */
    fun createNewGroup(groupName:String)
    {
        mRef = mDatabase.reference.child("Group")

        //show progress dialog
        showDialog("Creating","new Group....")

        mRef.child("Data").child("$groupName").setValue("").addOnCompleteListener {
            if(it.isSuccessful)
            {
                progressbar.dismiss()

                Log.d("newGroup","successful...")
            }
            else
            {
                progressbar.dismiss()

                Log.d("newGroup","Error...")
            }
        }
    }

    //show progress dialog
   protected fun showDialog(title:String , content:String)
    {
        progressbar.setTitle("$title")
        progressbar.setMessage("$content")
        progressbar.show()
    }
}

class ShowGroup(context: Context , groupNameShow: GroupNameShow): GroupManagement(context)
{
    protected val itemGroup = groupNameShow

    /*
    fech group name
    from firebase database
     */
    fun getGroupName()
    {
        //lsit item group name
        var item = mutableListOf<String>()

        //
        mRef = mDatabase.reference.child("Group/Data")
        mRef.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("groupName","${p0.toException()}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                item.clear()
                for (data in dataSnapshot.children)
                {
                    item.add(data.key.toString())
                }
                itemGroup.showGroupName(item)
                Log.d("groupName", "getData")
            }
        })

    }
}