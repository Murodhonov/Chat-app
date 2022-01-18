package uz.umarxon.chatgram2022.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import uz.umarxon.chatgram2022.databinding.LinearFromBinding
import uz.umarxon.chatgram2022.databinding.LinearTo2Binding
import uz.umarxon.chatgram2022.models.MessageClass

class MessageAdapter2(var list:List<MessageClass>, private val uid:String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(private var itemRv:LinearFromBinding):RecyclerView.ViewHolder(itemRv.root){
        fun onBind(messageClass:MessageClass){
            val matn = messageClass.text
            itemRv.text.text = matn
            itemRv.time.text = messageClass.date
        }
    }

    inner class ToVh(private var itemRv:LinearTo2Binding):RecyclerView.ViewHolder(itemRv.root){
        fun onBind(messageClass:MessageClass){
            val matn = messageClass.text
            Picasso.get().load("https://picsum.photo/500/500").into(itemRv.userImage)
            itemRv.text.text = matn
            itemRv.time.text = messageClass.date
        }
    }

    interface OnCLick{
        fun onLongClick(messageClass: MessageClass,img:CircleImageView,url:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1) FromVh(LinearFromBinding.inflate(LayoutInflater.from(parent.context),parent,false)) else ToVh(LinearTo2Binding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return if(getItemViewType(position) == 1) (holder as FromVh).onBind(list[position]) else (holder as ToVh).onBind(list[position])
    }

    override fun getItemViewType(position: Int): Int = if (list[position].fromUid == uid) 1 else 2

    override fun getItemCount(): Int = list.size
}