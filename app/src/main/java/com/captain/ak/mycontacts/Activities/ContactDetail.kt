package com.captain.ak.mycontacts.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.captain.ak.mycontacts.DataClass.ContactDTO
import com.captain.ak.mycontacts.R
import kotlinx.android.synthetic.main.activity_contact_detail.*

class ContactDetail : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)
        supportActionBar!!.hide()
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor= resources.getColor(R.color.fafafa_color)

        val bundle = intent.extras
        val contactDTO = bundle!!.getParcelable<ContactDTO>("data")

        textView_email.text = contactDTO!!.email
        textView_id.text = contactDTO.id
        textview_userFrag_phone.text = contactDTO.number
        if (contactDTO!=null) {
            imageview_userFrag_profileImage.setImageBitmap(contactDTO.image)
        }
        else
        {
            //imageview_userFrag_profileImage.setImageDrawable(resources.getDrawable(R.drawable.profile_image))

        }
        textview_userFrag_userName.text = contactDTO.name

        back.setOnClickListener { onBackPressed() }

    }
}
