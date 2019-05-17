package com.captain.ak.mycontacts.Adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.captain.ak.mycontacts.Activities.ContactDetail
import com.captain.ak.mycontacts.DataClass.ContactDTO
import com.captain.ak.mycontacts.R
import java.io.Serializable

class contactsRecyclerViewAdapter(items:List<ContactDTO>, val context: Context): RecyclerView.Adapter<contactsRecyclerViewAdapter.ViewHolder>() {

    private var list:List<ContactDTO> = items


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return contactsRecyclerViewAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.contacts_single_layout, p0, false))

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.contactText.text =list[p1].name

        val contactDTO = list[p1]

        p0.contactText.setOnClickListener {
            val intent = Intent(this.context,ContactDetail::class.java)
            val bundle = Bundle()
            bundle.putParcelable("data" , contactDTO)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        Log.i("Details" , list[p1].email+" "+list[p1].id+" "+list[p1].name+" "+list[p1].number)
        if (list[p1].image!=null)
        {
            p0.contactText1.visibility = View.INVISIBLE
            p0.contactImageView.visibility = View.VISIBLE
            p0.contactImageView.setImageBitmap(list[p1].image)
        }
        else
        {
            p0.contactText1.visibility = View.VISIBLE
            p0.contactImageView.visibility = View.INVISIBLE

        }

        if (p0.contactText.text.splitToSequence(" ").count().equals(2)) {
            p0.contactText1.text = p0.contactText.text.splitToSequence(" ").elementAt(0).elementAt(0).toString() + p0.contactText.text.splitToSequence(" ").elementAt(1).elementAt(0).toString()
        }
        else{
            p0.contactText1.text = p0.contactText.text.splitToSequence(" ").elementAt(0).elementAt(0).toString()

        }




    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val contactImageView = view.findViewById<ImageView>(R.id.contactImageView)

        val contactText = view.findViewById<TextView>(R.id.contactText)

        val contactText1 = view.findViewById<TextView>(R.id.contactText1)





    }

}