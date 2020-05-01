package app.file.chatapp.Entity

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserInfo(
    var userName:String? ="",
    var laseName:String?="",
    var nickName:String?="",
    var email:String?="",
    var uid:String?="",
    var imageURl:String?="",
    var device:String?=""
)
{
    @Exclude
    fun toMap():Map<String,Any?>
    {
        return mapOf(
            "UserName" to userName,
            "LastName" to  laseName,
            "NickName" to nickName,
            "Email" to email,
            "Uid" to uid,
            "Image" to imageURl,
            "DeviceToken" to device
        )
    }
}