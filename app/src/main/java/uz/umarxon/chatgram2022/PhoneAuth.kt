package uz.umarxon.chatgram2022

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import uz.ilhomjon.notificationfirebase5.rretrofit.ApiClient
import uz.ilhomjon.notificationfirebase5.rretrofit.ApiService
import uz.umarxon.chatgram2022.databinding.ActivityPhoneAuthBinding
import uz.umarxon.chatgram2022.models.User
import java.util.concurrent.TimeUnit

class PhoneAuth : AppCompatActivity() {

    lateinit var binding: ActivityPhoneAuthBinding
    lateinit var auth: FirebaseAuth
    private val TAG = "PhoneAuth"
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var myToken = ""
    lateinit var reference: DatabaseReference
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
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

        binding.etCode.visibility = View.GONE
        binding.code.visibility = View.GONE

        auth = FirebaseAuth.getInstance()

        binding.etNumber.setText("+998")

        binding.send.setOnClickListener {
            if (binding.etNumber.text.toString().length == 13 && binding.etName.text.isNotEmpty()){
                sendVerificationCode(binding.etNumber.text.toString())
                binding.send.visibility = View.GONE
                binding.etNumber.visibility = View.GONE
                binding.etName.visibility = View.GONE
                binding.etCode.visibility = View.VISIBLE
                binding.code.visibility = View.VISIBLE
            }else{
                Toast.makeText(
                    this,
                    "Telefon raqam to'gri ekanligini tekshiring",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.etCode.addTextChangedListener{
            verifyCode()
        }

        binding.etCode.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                verifyCode()
                val view = window.currentFocus
                if (view != null) {
                    val imm: InputMethodManager = binding.root.context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }else{
                    Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

    }

    override fun onBackPressed() {
        if (binding.etNumber.visibility == View.VISIBLE){
            binding.send.visibility = View.VISIBLE
            binding.etNumber.visibility = View.VISIBLE
            binding.etCode.visibility = View.GONE
            binding.code.visibility = View.GONE
        }else{
            super.onBackPressed()
        }
    }
    fun verifyCode(){
        val code = binding.etCode.text.toString()
        if (code.length == 6){
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (myToken.isNotEmpty()){

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user2 = task.result?.user

                        auth = FirebaseAuth.getInstance()
                        firebaseDatabase = FirebaseDatabase.getInstance()
                        reference = firebaseDatabase.getReference("users")

                        val user = User(user2!!.uid,binding.etName.text.toString(),binding.etNumber.text.toString(),"https://picsum.photos/500/500",false,myToken)

                        reference.child(auth.uid!!).setValue(user)

                        startActivity(Intent(this,HomeActivity::class.java).putExtra("user",user))
                    }

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this, "Kod xato kiritildi", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Muvaffaqiyatsiz!!!", Toast.LENGTH_SHORT).show()
                    }
                    // Update UI
                }
            }
    }

    private fun resentCode(phoneNimber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNimber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}