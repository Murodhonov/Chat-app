package uz.umarxon.chatgram2022.models

import java.io.Serializable

class User :Serializable{

    var uid:String? = null
    var name:String? = null
    var gmail:String? = null
    var image:String? = null
    var googleUser:Boolean? = null
    var userToken:String? = null

    constructor()

    constructor(uid: String?, name: String?, gmail: String?, image: String?, googleUser: Boolean?, userToken: String?) {
        this.uid = uid
        this.name = name
        this.gmail = gmail
        this.image = image
        this.googleUser = googleUser
        this.userToken = userToken
    }


    override fun toString(): String {
        return "User(uid=$uid, name=$name, gmail=$gmail, image=$image)"
    }
}