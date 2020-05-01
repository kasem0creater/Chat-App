package app.file.chatapp.BottomSheetManager

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import app.file.chatapp.R
import app.file.chatapp.UserLoginRegister.Usermanaget.getUserDataProfile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_profile.*

class BottomSheetAdapterProfile: BottomSheetDialogFragment() ,
    getUserDataProfile.getInfoUser
{

    fun newinstance():BottomSheetDialogFragment
    {
        val bottomsgeet =
            BottomSheetAdapterProfile()
        return bottomsgeet
    }

    private lateinit var userDataService: getUserDataProfile

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.layout_profile , container , false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //card setting
        settingCardBackground()
        //user instance service
        userDataService =
            getUserDataProfile(
                view.context,
                this
            )
        userDataService.getUserinfo(userDataService.getCurrentUser()?.uid.orEmpty())

        //click
        eventClick()

    }

    //card bg corner radius
    private fun settingCardBackground()
    {
        card_container_profile.setBackgroundResource(R.drawable.card_bg_profile)
        card_user_shared.setBackgroundResource(R.drawable.card_bg_shared)
    }

    override fun Data(userName: String, lastName: String, nickName: String, imageURL: String) {
        txt_profile_name.text = userName
        txt_profile_nick_name.text = nickName
        //image
        Glide.with(view!!.context).load(imageURL).into(image_profile_preview)

        Log.d("profile","$userName")
    }

    //all event click
    private fun eventClick()
    {
        //image click show edit profile
        btn_show_edit_profile.setOnClickListener {
            userDataService.startEditActivity()
        }


        //btn click close profile
        btn_close_profile.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.wtf("Preview Profile","Deastroy")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.wtf("Preview Profile","Deastroy")
    }
}