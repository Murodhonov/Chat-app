package uz.umarxon.chatgram2022.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.umarxon.chatgram2022.Data.selectedUsers
import uz.umarxon.chatgram2022.databinding.ItemRvCheckboxBinding
import uz.umarxon.chatgram2022.models.User

class RvAdapter2(private val list: List<User>) :
    RecyclerView.Adapter<RvAdapter2.Vh>() {
    inner class Vh(var itemRv: ItemRvCheckboxBinding) : RecyclerView.ViewHolder(itemRv.root) {
        fun onBind(user: User, position: Int) {

            Picasso.get().load(user.image).into(itemRv.image)
            itemRv.tvName.text = user.name
            itemRv.tvMessage.text = user.gmail

            itemRv.root.setOnClickListener {
                itemRv.checkbox.performClick()
            }

            if (!itemRv.checkbox.isChecked){
                itemRv.checkbox.visibility = View.GONE
            }else{
                itemRv.checkbox.visibility = View.VISIBLE
            }

            itemRv.checkbox.setOnCheckedChangeListener { compoundButton, b ->
                if (b){
                    itemRv.checkbox.visibility = View.VISIBLE
                    itemRv.checkbox.isChecked = true
                    selectedUsers.add(user)
                }else{
                    itemRv.checkbox.isChecked = false
                    itemRv.checkbox.visibility = View.GONE
                    for (i in selectedUsers){
                        if (i.uid == user.uid){
                            selectedUsers.remove(i)
                            break
                        }
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}