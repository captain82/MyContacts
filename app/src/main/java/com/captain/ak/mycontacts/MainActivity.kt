package com.captain.ak.mycontacts

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import com.captain.ak.mycontacts.Adapter.contactsRecyclerViewAdapter
import com.captain.ak.mycontacts.DataClass.ContactDTO
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import android.R.id
import android.os.Build
import android.annotation.SuppressLint
import android.R.attr.phoneNumber
import android.R.attr.name
import android.text.TextUtils








class MainActivity : AppCompatActivity() {

    private val DISPLAY_NAME = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Contacts.DISPLAY_NAME

    private val FILTER = "$DISPLAY_NAME NOT LIKE '%@%'"

    private val ORDER = String.format("%1\$s COLLATE NOCASE", DISPLAY_NAME)

    @SuppressLint("InlinedApi")
    private val PROJECTION =
        arrayOf(ContactsContract.Contacts._ID, DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER,ContactsContract.Contacts.PHOTO_URI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onResume() {
        super.onResume()

        //setUpRecyclerView()
        setUpRecyclerView1()
    }

    private fun setUpRecyclerView1() {

        val contacts:MutableList<ContactDTO> = ArrayList()

        val cr = applicationContext.contentResolver

        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER)

        if (cursor != null && cursor.moveToFirst()) {

            do {
                // get the contact's information
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME))
                val hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                // get the user's email address
                var email: String? = ""
                val ce = cr.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(id), null
                )
                if (ce != null && ce.moveToFirst()) {
                    email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    Log.i("Email" ,email)
                    ce.close()
                }

                // get the user's phone number
                var phone: String? = ""
                if (hasPhone > 0) {
                    val cp = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null
                    )
                    if (cp != null && cp.moveToFirst()) {
                        phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        cp.close()
                    }
                }



                val photoUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))



                // if the user user has an email or phone then add it to contacts
                if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                            && !email!!.equals(name, ignoreCase = true)) || !TextUtils.isEmpty(phone)
                ) {
                    val contact = ContactDTO()
                    contact.name = name
                    contact.id = id
                    contact.email = email!!
                    contact.number = phone!!
                    if (photoUri != null){
                        contact.image = MediaStore.Images.Media.getBitmap(cr, Uri.parse(photoUri))
                    }
                    contacts.add(contact)
                }

            } while (cursor.moveToNext())

            // clean up cursor
            cursor.close()
        }

        val mAdapterView = contactsRecyclerViewAdapter(contacts, this)
        contacts_recycler_view.adapter = mAdapterView
        contacts_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    }

    private fun setUpRecyclerView() {

        val contentResolver: ContentResolver =applicationContext.contentResolver

        val contactList: MutableList<ContactDTO> = ArrayList()


        val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC")


        while (contacts.moveToNext() ) {
            val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

            val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val id = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID))

            val cur1 = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                arrayOf(id), null
            )

            while (cur1.moveToNext())
            {
                val email:String = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                Log.i("Email" ,name+ email)
            }

            val obj = ContactDTO()

            obj.name = name

            obj.number = number

            obj.id = id

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
