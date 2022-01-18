package uz.umarxon.chatgram2022.models

import java.io.Serializable

class GroupClass:Serializable {
    var id:String? = null
    var name:String? = null
    var usersList:ArrayList<User>? = null
    var fromUid:String? = null
    var toUid:String? = null

    constructor(
        id: String?,
        name: String?,
        usersList: ArrayList<User>?,
        fromUid: String?,
        toUid: String?
    ) {
        this.id = id
        this.name = name
        this.usersList = usersList
        this.fromUid = fromUid
        this.toUid = toUid
    }

    constructor()

    override fun toString(): String {
        return "GroupClass(id=$id, name=$name, usersList=$usersList, fromUid=$fromUid, toUid=$toUid)"
    }


}