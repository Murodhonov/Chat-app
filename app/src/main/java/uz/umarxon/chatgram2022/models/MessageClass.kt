package uz.umarxon.chatgram2022.models

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class MessageClass {

    var id:String? = null
    var text:String? = null
    var fromUid:String? = null
    var toUid:String? = null

    @SuppressLint("SimpleDateFormat")
    var date  = SimpleDateFormat("HH:mm:ss").format(Date())

    constructor(id: String?, text: String?, fromUid: String?, toUid: String?) {
        this.id = id
        this.text = text
        this.fromUid = fromUid
        this.toUid = toUid
    }

    constructor()

    override fun toString(): String {
        return "MessageClass(id=$id, text=$text, fromUid=$fromUid, toUid=$toUid, date='$date')"
    }
}