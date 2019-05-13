package com.captain.ak.mycontacts

import android.content.ContentResolver
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import com.captain.ak.mycontacts.Adapter.contactsRecyclerViewAdapter
import com.captain.ak.mycontacts.DataClass.ContactDTO
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onResume() {
        super.onResume()

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {

        val contentResolver: ContentResolver =applicationContext.contentResolver

        val contactList: MutableList<ContactDTO> = ArrayList()


        val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC")

        while (contacts.moveToNext()) {
            val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

            val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val obj = ContactDTO()

            obj.name = name

            obj.number = number

            val photoUri = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
            if (photoUri != null) {
                obj.image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photoUri))
            }

            contactList.add(obj)


        }


        val mAdapterView = contactsRecyclerViewAdapter(contactList, this)
        contacts_recycler_view.adapter = mAdapterView
        contacts_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        contacts.close()


    }


}
