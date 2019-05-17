package com.captain.ak.mycontacts.DataClass

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class ContactDTO() : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest.writeString(number)
        dest.writeParcelable(image, flags)
        dest.writeString(id)
        dest.writeString(email)
    }


    var name:String? = ""

    var number:String? = ""

    var image: Bitmap? = null

    var id: String? = ""

    var email: String? = ""

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        number = parcel.readString()
        image = parcel.readParcelable(Bitmap::class.java.classLoader)
        id = parcel.readString()
        email = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<ContactDTO> {
        override fun createFromParcel(parcel: Parcel): ContactDTO {
            return ContactDTO(parcel)
        }

        override fun newArray(size: Int): Array<ContactDTO?> {
            return arrayOfNulls(size)
        }
    }
}