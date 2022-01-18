package uz.umarxon.chatgram2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import uz.umarxon.chatgram2022.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {

    lateinit var binding:ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Picasso.get().load(intent.getStringExtra("url")).into(binding.bigImage)



    }
}