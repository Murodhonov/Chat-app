package uz.umarxon.chatgram2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.umarxon.chatgram2022.adapter.MessageAdapter2
import uz.umarxon.chatgram2022.databinding.ActivityMessageBinding
import uz.umarxon.chatgram2022.models.GroupClass
import uz.umarxon.chatgram2022.models.MessageClass

class GroupActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var referenceMessage:DatabaseReference
    lateinit var binding: ActivityMessageBinding
    lateinit var messageAdapter: MessageAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var list: ArrayList<MessageClass>
        val group = intent.getSerializableExtra("keyGroup") as GroupClass

        binding.back.setOnClickListener {
            onBackPressed()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceMessage = firebaseDatabase.getReference("messages")

        binding.send.setOnClickListener {
            val key = referenceMessage.push().key
            val messageText = binding.text.text.toString().trim()
            binding.text.text.clear()
            if (messageText.isNotEmpty()){
                val message = MessageClass(key,messageText,firebaseAuth.uid,group.id)
                referenceMessage.child(key!!).setValue(message)
                binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount-1)
                Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
            }
        }

        referenceMessage.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(MessageClass::class.java)
                    if(value!=null){
                        if ((value.fromUid==firebaseAuth.uid && value.toUid==group.id)){
                            list.add(value)
                        }else {
                            for (i in group.usersList!!) {
                                if ((value.fromUid == i.uid && value.toUid == group.id)) {
                                    list.add(value)
                                    break
                                }
                            }
                        }
                    }
                }
                messageAdapter = MessageAdapter2(list,firebaseAuth.uid!!)
                binding.rv.adapter = messageAdapter
                binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}