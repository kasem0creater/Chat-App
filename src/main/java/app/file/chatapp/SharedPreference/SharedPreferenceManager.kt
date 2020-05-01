package app.file.chatapp.SharedPreference

import android.content.Context
import android.net.Uri

class SharedPreferenceManager(context: Context)
{
    private val SHARE_NAME="SHARED_NAME_MANAGET"
    private val shared = context.getSharedPreferences(SHARE_NAME ,Context.MODE_PRIVATE)
    private val edit = shared.edit()


   private val _status ="USER_STATUS"
    fun writeStateCurrentUser(status:Boolean)
    {
        edit.putBoolean(_status , status)
        edit.apply()
    }
    fun getStatusUser():Boolean
    {
        return shared.getBoolean(_status,false)
    }

    // user name
    private val _userName="USER_NAME"
    fun writeUserName(userName:String)
    {
        edit.putString(_userName , userName)
        edit.apply()
    }
    fun getUserName():String
    {
        return shared.getString(_userName,"").toString()
    }
    private val _imageUri = "IMAGE_URI"
    fun writeImageURI(imageUri:Uri)
    {
        edit.putString(_imageUri , imageUri.toString())
        edit.apply()
    }

    fun getImageUri():String
    {
        return shared.getString(_imageUri,"").toString()
    }
    private val notifyId="NOTIFYID"
    fun writeNotify(state:Boolean)
    {
        edit.putBoolean(notifyId ,state)
        edit.apply()
    }
    fun getNotify():Boolean
    {
        return shared.getBoolean(notifyId,false)
    }
    val statusOnline="ONLINE_OFFLINE_STATUST"
    fun writeStatustOnline(state: Boolean)
    {
        edit.putBoolean(statusOnline,state)
        edit.apply()
    }
    fun getStatusUserOnline():Boolean
    {
        val state = shared.getBoolean(statusOnline , false)
        return  state
    }
    fun clear()
    {
        //edit.remove(key value)
        edit.clear()
        edit.apply()
    }
}