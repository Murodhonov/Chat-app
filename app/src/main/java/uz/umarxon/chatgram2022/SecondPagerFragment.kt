package uz.umarxon.chatgram2022

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uz.umarxon.chatgram2022.adapter.GroupListAdapter
import uz.umarxon.chatgram2022.databinding.FragmentSecondPagerBinding
import uz.umarxon.chatgram2022.models.GroupClass

class SecondPagerFragment() : Fragment() {

    lateinit var binding:FragmentSecondPagerBinding
    lateinit var reference2:DatabaseReference
    lateinit var array2:ArrayList<GroupClass>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSecondPagerBinding.inflate(layoutInflater)

        reference2 = FirebaseDatabase.getInstance().getReference("groups")

        reference2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                array2 = ArrayList()
                val children1 = snapshot.children
                for(child in children1){
                    val value2 = child.getValue(GroupClass::class.java)
                    if (value2!=null){
                        for (i in value2.usersList!!){
                            if (i.uid == FirebaseAuth.getInstance().uid){
                                array2.add(value2)
                                break
                            }
                        }
                    }
                }

                val adapter = GroupListAdapter(array2,object : GroupListAdapter.OnClick{
                    override fun onClick(group: GroupClass) {
                        startActivity(Intent(context,GroupActivity::class.java).putExtra("keyGroup",group))
                    }
                })
                binding.rv.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }


}