package uz.umarxon.chatgram2022

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import uz.ilhomjon.notificationfirebase5.rretrofit.ApiClient
import uz.ilhomjon.notificationfirebase5.rretrofit.ApiService
import uz.umarxon.chatgram2022.databinding.ActivityMainBinding
import uz.umarxon.chatgram2022.models.User

class MainActivity : AppCompatActivity() {

    var RC_SIGN_IN = 1
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMainBinding
    lateinit var reference: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var googleSignInClient:GoogleSignInClient
    private val TAG = "MainActivity"
    var myToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build())

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser!=null){
            if (auth.currentUser!!.email!=""){
                startActivity(Intent(this,HomeActivity::class.java).putExtra("user",User(auth.uid,auth.currentUser?.displayName,auth.currentUser?.email,auth.currentUser?.photoUrl.toString(),true,myToken)))
                finish()
            }else{
                try{
                    startActivity(Intent(this,HomeActivity::class.java).putExtra("user",User(auth.uid,auth.currentUser?.displayName,auth.currentUser?.email,"https://picsum.photos/200/300",true,myToken)))
                    finish()
                }catch (e:Exception){

                }
            }
        }

        binding.btnSignGoogle.setOnClickListener {
            signIn()
        }
        binding.btnPhone.setOnClickListener {
            startActivity(Intent(this,PhoneAuth::class.java))
        }
    }


    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

                //Picasso.get().load(account.photoUrl).into(binding.iv)

                //binding.tv.text = "Display name: ${account.displayName}\n\nEmail: ${account.email}\n\nFamily name: ${account.familyName}\n\nGiven Name: ${account.givenName}\n\nGranted Scope${account.grantedScopes}\n\nID:${account.id}\n\nID Token: ${account.idToken}\n\nIs Expired: ${account.isExpired}\n\nRequested Scope: ${account.requestedScopes}\n\nServer Auth Code: ${account.serverAuthCode}\n\n"
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (myToken.isNotEmpty()){

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
//                    updateUI(user)

                        auth = FirebaseAuth.getInstance()
                        firebaseDatabase = FirebaseDatabase.getInstance()
                        reference = firebaseDatabase.getReference("users")

                        val user = User(auth.uid,auth.currentUser?.displayName,auth.currentUser?.email,auth.currentUser?.photoUrl.toString(),true,myToken)

                        reference.child(auth.uid!!).setValue(user)

                        startActivity(Intent(this,HomeActivity::class.java).putExtra("user",user))
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
                    Toast.makeText(this, "Error \n${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}