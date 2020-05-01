package app.file.chatapp.Recyclemanaget.chat

interface ChatListen
{
    fun onClick(position:Int , name:String ,nickName:String, uid:String , image:String)
    fun onLong(position:Int , name:String ,nickName:String, uid:String, image:String)
}