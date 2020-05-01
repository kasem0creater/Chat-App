package app.file.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import app.file.chatapp.BottomSheetManager.BottomSheetAdapterProfile
import app.file.chatapp.CustomDialog.DialogNewGroup
import app.file.chatapp.FriendsManagement.FindFriendsActivity
import app.file.chatapp.SharedPreference.SharedPreferenceManager
import app.file.chatapp.UserLoginRegister.Usermanaget.UserManageService
import app.file.chatapp.UserLoginRegister.Usermanaget.getUserDataProfile
import app.file.chatapp.ViewPagerManaget.ViewPagerAdapter
import app.file.chatapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_setting.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() , getUserDataProfile.getInfoUser {

    private lateinit var userService: UserManageService
    private lateinit var shared: SharedPreferenceManager
    protected lateinit var dataProfile: getUserDataProfile
    private val viewpagerAdapter =
        ViewPagerAdapter(
            supportFragmentManager
        )
    private lateinit var mRef:DatabaseReference


    //bottom sheet
    private  lateinit var bottomsheetSetting:LinearLayout
    private lateinit var sheetBehaviorSetting:BottomSheetBehavior<LinearLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set instance user service
        userService =
            UserManageService(
                this@MainActivity
            )
        dataProfile =
            getUserDataProfile(
                this@MainActivity,
                this
            )
        shared =
            SharedPreferenceManager(this@MainActivity)

        //add toolbar
        settingAppBar()


        //view pager
        settingViewPager()

        //initial bottom sheet all by id
        initailBottomsheet()


        //bottom navigate click menu select page
        bottomNaviagationClick()


        //card view rasius
        settingCardBackground()

        //click item in bottom sheet setting all Event
        bottomSheetSsettingItemClick()

    }

    override fun onStart() {
        super.onStart()

        if (shared.getStatusUser() == false) {
            userService.startLoginActivity()
        }
        else
        {
            updateUserStatus("online")
        }

        //check system notify
        // if have go to request friend list pages
        if(shared.getNotify() == true)
        {
            viewPager_container.currentItem = 3
            Log.i("from notify to","page reques friend"+shared.getNotify())
            shared.writeNotify(false)
        }
        else
        {
            shared.writeNotify(false)
        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if(it.isSuccessful)
            {
                val token = it.result?.token
                Log.i("Token", token)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (userService.getCurrentUser()?.uid != null) {
            updateUserStatus("offline")
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        if (userService.getCurrentUser()?.uid != null) {
            updateUserStatus("offline")
        }

    }

    override fun onBackPressed() {
        bottomSheetSettingApp()
    }
    /*
    setting app abr
     */
    private fun settingAppBar() {
        var toolbar = findViewById(R.id.custom_toolBar) as Toolbar
        toolbar.title = "Chat App"
        //toolbar.setNavigationIcon()
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main_setting, menu)
        return true
    }

    //menu select item click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {
                bottomSheetSettingApp()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /*
    setting viewpager show fragment
     -chat
     -group
     -contact
     */
    private fun settingViewPager() {
        viewPager_container.adapter = viewpagerAdapter
        viewPager_container.currentItem = 0
    }


    /*
    show user info
    in bottom sheet
    preview profile
     */
    override fun Data(userName: String, lastName: String, nickName: String, imageURL: String) {

    }

    /*
    botton navigation menu click
     -next page
       - chat
       - group
       - contact
     */
    private fun bottomNaviagationClick() {
        navigation_main.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> {
                   viewPager_container.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_group -> {
                   viewPager_container.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_contact -> {
                    viewPager_container.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }


    /*
    initial bottom sheet
     */
    private fun initailBottomsheet()
    {
        //bottom sheet setting
        bottomsheetSetting = findViewById(R.id.bottomSheet_linear_setting)
        sheetBehaviorSetting = BottomSheetBehavior.from(bottomsheetSetting)
    }

    /*
    setting bottom sheetsetting
     -fun in bottom sheet
        - show profile
        - create group
        -find friend
     */

    private fun bottomSheetSettingApp() {

        sheetBehaviorSetting.peekHeight = 600
        sheetBehaviorSetting.state = BottomSheetBehavior.STATE_EXPANDED
    }

    /*
    card background corner radius
      - bottom sheet
     */
    private fun settingCardBackground()
    {
        card_profile.setBackgroundResource(R.drawable.bottomsheet_background)
        card_group.setBackgroundResource(R.drawable.bottomsheet_group)
        card_logout.setBackgroundResource(R.drawable.bottomsheet_logout)
        card_find_friend.setBackgroundResource(R.drawable.bottomsheet_find_friend)
    }

    /*
    click item in bottom sheet setting
      -account
      -group
      - logout
     */
    private fun bottomSheetSsettingItemClick()
    {
        //- logout
        btn_logout_user.setOnClickListener {
            userService.logOut()

            //close bottom sheet setting
            //alter click action menu
            sheetBehaviorSetting.state = BottomSheetBehavior.STATE_HIDDEN
        }

        //show dialog create group
        btn_show_dialog_group.setOnClickListener {
            val dialog = DialogNewGroup()
            dialog.show(supportFragmentManager,"")

            //close bottom sheet setting
            //alter click action menu
            sheetBehaviorSetting.state = BottomSheetBehavior.STATE_HIDDEN

        }

        //show preview profile
        btn_show_profile.setOnClickListener {
            val dialog = BottomSheetAdapterProfile()
                .newinstance()
            dialog.show(supportFragmentManager,"")

            sheetBehaviorSetting.state = BottomSheetBehavior.STATE_HIDDEN
        }

        //btn find friend click open
        // find friend activity
        btn_show_find_friend.setOnClickListener {
            startActivity(Intent(this , FindFriendsActivity::class.java))
        }
    }

    /*
    write user state online and offline
     */
    private fun updateUserStatus(status:String)
    {
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("mm dd yyyy")
        val currentTime = SimpleDateFormat("hh:mm a")

        val date = currentDate.format(calendar.time)
        val time = currentTime.format(calendar.time)

        val mapOnline:MutableMap<String,Any> = hashMapOf()
        mapOnline.put("time",time)
        mapOnline.put("date",date)
        mapOnline.put("status","${status}")

        mRef = FirebaseDatabase.getInstance().reference.child("UserInfo/Data")
        mRef.child(userService.getCurrentUser()?.uid.toString()).child("UserStatus").
                updateChildren(mapOnline)

    }

}
