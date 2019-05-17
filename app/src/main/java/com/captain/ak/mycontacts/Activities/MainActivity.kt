package com.captain.ak.mycontacts.Activities

import android.Manifest
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
import android.util.Log
import android.os.Build
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.app.ProgressDialog
import android.R
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private val DISPLAY_NAME = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    else
        ContactsContract.Contacts.DISPLAY_NAME

    private val FILTER = "$DISPLAY_NAME NOT LIKE '%@%'"

    private val ORDER = String.format("%1\$s COLLATE NOCASE", DISPLAY_NAME)

    @SuppressLint("InlinedApi")
    private val PROJECTION =
        arrayOf(
            ContactsContract.Contacts._ID,
            DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_URI
        )

    val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.captain.ak.mycontacts.R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            MY_PERMISSIONS_REQUEST_READ_CONTACTS ->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadContacts()
                    Toast.makeText(this , "Permision Granted" , Toast.LENGTH_SHORT).show()
                } else {

                }
                return
            }
        }
    }

    //***********************fun to load contacts***********************************************************//


    fun loadContacts()
    {
        object : AsyncTask<Void, Void, ArrayList<ContactDTO>>() {

            val progressDialog = ProgressDialog(this@MainActivity)




            override fun onPreExecute()
            {
                progressDialog.setMessage("Please Wait")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
            }

            @SuppressLint("WrongThread")
            override fun doInBackground(vararg params: Void): ArrayList<ContactDTO>? {
                setUpRecyclerView1()
                return null

            }// End of doInBackground method

            override fun onPostExecute(result: ArrayList<ContactDTO>?) {
                if (progressDialog.isShowing)
                {
                    progressDialog.dismiss()
                }

            }//End of onPostExecute method
        }.execute()

    }



    private fun setUpRecyclerView1() {

        val contacts: MutableList<ContactDTO> = ArrayList()

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
                    Log.i("Email", email)
                    ce.close()
                }
                //get the users phone number
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
                            && !email!!.equals(name, ignoreCase = true)) || !TextUtils.isEmpty(phone)) {
                    val contact = ContactDTO()
                    contact.name = name
                    contact.id = id
                    contact.email = email!!
                    contact.number = phone!!
                    if (photoUri != null) {
                        contact.image = MediaStore.Images.Media.getBitmap(cr, Uri.parse(photoUri))
                    }
                    contacts.add(contact)
                }
            } while (cursor.moveToNext())

            cursor.close()

        }

        runOnUiThread {
            val mAdapterView = contactsRecyclerViewAdapter(contacts, this)
            contacts_recycler_view.adapter = mAdapterView
            contacts_recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }


    }


}
