package uz.umarxon.chatgram2022

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import uz.umarxon.chatgram2022.adapter.ViewPagerAdapter1
import uz.umarxon.chatgram2022.databinding.ActivityHomeBinding
import uz.umarxon.chatgram2022.models.GroupClass
import uz.umarxon.chatgram2022.models.User

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var reference: DatabaseReference
    lateinit var reference2: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var auth:FirebaseAuth
    lateinit var array:ArrayList<User>
    lateinit var array2:ArrayList<GroupClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        reference2 = firebaseDatabase.getReference("groups")

        val user = intent.getSerializableExtra("user") as User

        Picasso.get().load(user.image).into(binding.userImage)

        binding.newGroup.setOnClickListener {
            startActivity(Intent(this,NewGroupActivity::class.java).putExtra("user",user))
        }

        binding.userImage.setOnClickListener {
            startActivity(Intent(this,ImageViewerActivity::class.java).putExtra("url",user.image),
                ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(binding.userImage, "img")).toBundle())
        }

        binding.logOut.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Users"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Groups"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

        reference.addValueEventListener(object : ValueEventListener{
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
                refresh(user,array)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun changeAdapter(array:ArrayList<User>,user:User){
        binding.viewPager.adapter = ViewPagerAdapter1(supportFragmentManager,array, user)
    }



    override fun onBackPressed() {
        finishAffinity()
    }
    fun refresh(user:User,array:ArrayList<User>){
        reference2.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                array2 = ArrayList()
                val children1 = snapshot.children
                for(child in children1){
                    val value2 = child.getValue(GroupClass::class.java)
                    if (value2!=null){
                         array2.add(value2)
                    }
                }

                changeAdapter(array,user)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}