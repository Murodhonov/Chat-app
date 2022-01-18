package uz.umarxon.chatgram2022

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.umarxon.chatgram2022.adapter.RvAdapter
import uz.umarxon.chatgram2022.databinding.FragmentMainViewPagerBinding
import uz.umarxon.chatgram2022.models.User

class MainViewPagerFragment(var list: List<User>) : Fragment() {

    lateinit var binding:FragmentMainViewPagerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentMainViewPagerBinding.inflate(layoutInflater)

        val adapter = RvAdapter(list,object : RvAdapter.OnClick{
            override fun onClick(user: User) {
                startActivity(Intent(context,MessageActivity::class.java).putExtra("keyUser",user))
            }
        })
        binding.rv.adapter = adapter

        return binding.root
    }


}