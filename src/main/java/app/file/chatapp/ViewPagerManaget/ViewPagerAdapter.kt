package app.file.chatapp.ViewPagerManaget

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import app.file.chatapp.Chat.chatu.ChatFragment
import app.file.chatapp.Chat.contact.ContactFragment
import app.file.chatapp.Chat.group.GroupFragment
import app.file.chatapp.FriendsManagement.RequestFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment? {
        when(position)
        {
            0 -> {return ChatFragment().newIntance()}
            1 -> {return GroupFragment().newIntance()}
            2 -> {return ContactFragment().newIntance()}
            3 -> {return RequestFragment().newInstance()}
            else ->
            {
                return null
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {

        when(position)
        {
            0 -> {return "Chat"}
            1 -> {return "Group"}
            2 -> {return "Friend"}
            3 ->{return "Requests"}
            else ->
            {
                return null
            }
        }
    }
}