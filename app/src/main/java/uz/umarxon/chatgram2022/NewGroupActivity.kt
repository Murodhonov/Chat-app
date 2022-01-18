package uz.umarxon.chatgram2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.umarxon.chatgram2022.Data.selectedUsers
import uz.umarxon.chatgram2022.adapter.RvAdapter2
import uz.umarxon.chatgram2022.models.GroupClass
import uz.umarxon.chatgram2022.models.User
import uz.umarxon.chatgram2022.databinding.ActivityNewGroupBinding

class NewGroupActivity : AppCompatActivity() {

    lateinit var binding : ActivityNewGroupBinding
    lateinit var reference: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var array:ArrayList<User>
    var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var user = intent.getSerializableExtra("user") as User

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        val reference2 = firebaseDatabase.getReference("groups")
        reference = firebaseDatabase.getReference("users")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                array = ArrayList()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value!=null){
                        if (value.uid != auth.uid){
                            array.add(value)
                        }
                    }

                }
                val adapter = RvAdapter2(array)
                binding.rv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.createGroup.setOnClickListener {
            if (selectedUsers.size>0){
                    if (binding.nameGroup.text.isNotEmpty()){
                        val key = reference2.push().key
                        val list: java.util.ArrayList<User> = selectedUsers
                        list.add(user)
                        val name = binding.nameGroup.text.toString()
                        if (name.isNotEmpty()) {
                            val user3 = GroupClass(key,name,list, auth.uid, key)

                            reference2.child(key!!).setValue(user3)
                            Toast.makeText(this, "Guruh yaratildi !!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }else{
                        Toast.makeText(this, "Guruh nomi kiritilmadi !!", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this, "User tanlanmadi!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}