package uz.umarxon.chatgram2022

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.ilhomjon.notificationfirebase5.models.Data
import uz.ilhomjon.notificationfirebase5.models.MyResponse
import uz.ilhomjon.notificationfirebase5.models.Sender
import uz.ilhomjon.notificationfirebase5.rretrofit.ApiClient
import uz.ilhomjon.notificationfirebase5.rretrofit.ApiService
import uz.umarxon.chatgram2022.adapter.MessageAdapter
import uz.umarxon.chatgram2022.databinding.ActivityMessageBinding
import uz.umarxon.chatgram2022.models.MessageClass
import uz.umarxon.chatgram2022.models.User

class MessageActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var referenceMessage:DatabaseReference
    lateinit var binding: ActivityMessageBinding
    lateinit var messageAdapter: MessageAdapter
    var myToken = ""
    private val TAG = "MessageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        window.statusBarColor = Color.parseColor("#527DA3")
        setContentView(binding.root)

        val apiService = ApiClient.getRetrofit("https://fcm.googleapis.com/").create(ApiService::class.java)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task->
            if (!task.isSuccessful){
                Log.d(TAG, "onCreate: token falled")
                return@OnCompleteListener
            }
            val token = task.result
            Log.d(TAG, token ?: "")
            myToken = token!!
        })

        var list: ArrayList<MessageClass>
        val user = intent.getSerializableExtra("keyUser") as User

        try {
            Picasso.get().load(user.image).into(binding.imageUser)
        }catch (e:Exception){
            binding.imageUser.setImageResource(R.mipmap.ic_launcher)
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.imageUser.setOnClickListener {
            startActivity(
                Intent(this,ImageViewerActivity::class.java).putExtra("url",user.image),
                ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(it, "img")).toBundle())
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        referenceMessage = firebaseDatabase.getReference("messages")

            binding.send.setOnClickListener {
                val key = referenceMessage.push().key
                val messageText = binding.text.text.toString().trim()
                binding.text.text.clear()
                if (messageText.isNotEmpty()){
                    val message = MessageClass(key,messageText,firebaseAuth.uid,user.uid)
                    referenceMessage.child(key!!).setValue(message)
                    binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount-1)

                    apiService.sendNotification(
                        Sender(
                            Data(
                                firebaseAuth.currentUser!!.displayName!!,
                                user.image!!,
                                messageText,
                                "New Message",
                                user.name!!
                            ),
                            user.userToken!!
                        )
                    ).enqueue(object : Callback<MyResponse> {
                        override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                            if (response.isSuccessful){

                            }
                        }

                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            Toast.makeText(this@MessageActivity, "Error", Toast.LENGTH_SHORT).show()
                        }
                    })

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
                        if ((value.fromUid==firebaseAuth.uid && value.toUid==user.uid) || (value.fromUid==user.uid && value.toUid==firebaseAuth.uid)){
                            list.add(value)
                        }
                    }
                }
                messageAdapter = MessageAdapter(list,firebaseAuth.uid!!)
                binding.rv.adapter = messageAdapter
                binding.rv.scrollToPosition(binding.rv.adapter!!.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

   /* fun databaseChangeListner(list:ArrayList<MessageClass>,user:User){
        referenceMessage.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(MessageClass::class.java)
                    if(value!=null){
                        if ((value.fromUid==firebaseAuth.uid && value.toUid==user.uid) || (value.fromUid==user.uid && value.toUid==firebaseAuth.uid)){
                            list.add(value)
                        }
                    }
                }

                binding.rv.smoothScrollToPosition(list.size-1)
                messageAdapter = MessageAdapter(list,firebaseAuth.uid!!,user.image.toString(),firebaseAuth.currentUser!!.photoUrl.toString(),object : MessageAdapter.OnCLick{
                    override fun onLongClick(messageClass: MessageClass,img:CircleImageView,url:String) {
                        startActivity(Intent(this@MessageActivity,ImageViewerActivity::class.java).putExtra("url",url),
                            ActivityOptions.makeSceneTransitionAnimation(this@MessageActivity,
                                Pair.create(img, "img")).toBundle())
                    }
                })
                binding.rv.adapter = messageAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }*/
}