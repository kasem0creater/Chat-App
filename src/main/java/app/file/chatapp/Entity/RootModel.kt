package app.file.chatapp.Entity

import com.google.gson.annotations.SerializedName

data class RootModel
    (
    @SerializedName("to") var to:String = "",
    @SerializedName("notification") var notification: Notification
)
