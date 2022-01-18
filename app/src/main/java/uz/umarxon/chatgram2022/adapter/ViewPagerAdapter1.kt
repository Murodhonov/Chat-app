package uz.umarxon.chatgram2022.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import uz.umarxon.chatgram2022.MainViewPagerFragment
import uz.umarxon.chatgram2022.SecondPagerFragment
import uz.umarxon.chatgram2022.models.User

class ViewPagerAdapter1(fragmentManager: FragmentManager,var list:ArrayList<User>,var user:User): FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment {
        return  if (position==0){
            MainViewPagerFragment(list)
        }else if (position == 1){
            SecondPagerFragment()
        }else{
            Fragment()
        }
    }
}