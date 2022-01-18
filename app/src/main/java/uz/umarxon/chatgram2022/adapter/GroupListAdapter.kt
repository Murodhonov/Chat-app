package uz.umarxon.chatgram2022.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.umarxon.chatgram2022.databinding.ItemRvBinding
import uz.umarxon.chatgram2022.models.GroupClass
import kotlin.random.Random

class GroupListAdapter(private val list: List<GroupClass>, var onClick:OnClick) :
    RecyclerView.Adapter<GroupListAdapter.Vh>() {
    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root) {
        fun onBind(group: GroupClass, position: Int) {

            itemRv.tvName.text = group.name
            itemRv.tvMessage.text = group.usersList!!.size.toString()

            itemRv.root.setOnClickListener {
                onClick.onClick(group)
            }

            Picasso.get().load(getImage()).into(itemRv.image)

        }
    }

    fun getImage():String{
        return when(Random.nextInt(0,5)){
            0->{
                "https://picsum.photos/500/500"
            }
            1->{
                "https://picsum.photos/400/400"
            }
            2->{
                "https://picsum.photos/600/600"
            }
            3->{
                "https://picsum.photos/2000/2000"
            }
            4->{
                "https://picsum.photos/1024/900"
            }
            5->{
                "https://picsum.photos/550/550"
            }
            else -> {""}
        }
    }

    interface OnClick {
        fun onClick(group: GroupClass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}