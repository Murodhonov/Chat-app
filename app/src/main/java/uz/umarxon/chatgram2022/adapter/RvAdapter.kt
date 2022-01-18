package uz.umarxon.chatgram2022.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.umarxon.chatgram2022.databinding.ItemRvBinding
import uz.umarxon.chatgram2022.models.User

class RvAdapter(private val list: List<User>,var onClick:OnClick) :
    RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root) {
        fun onBind(user: User, position: Int) {

            Picasso.get().load(user.image).into(itemRv.image)
            itemRv.tvName.text = user.name
            itemRv.tvMessage.text = user.gmail

            itemRv.root.setOnClickListener {
                onClick.onClick(user)
            }

        }
    }

    interface OnClick {
        fun onClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}