package app.file.chatapp.Recyclemanaget.firend

import app.file.chatapp.Entity.Contacts


interface FindFirendListen
{
    fun onClick(position:Int , item: Contacts)
    fun  onLongClick(position:Int , item: Contacts)
}