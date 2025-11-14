package ir.lifeplus.gamenethelper.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class GenericAdapter<T>(private val data: List<T>, @LayoutRes private val layoutId: Int, private val bind: (View, T) -> Unit, private val onClick: ((T) -> Unit)? = null, private val onLongClick: ((T) -> Unit)? = null) : RecyclerView.Adapter<GenericAdapter<T>.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(item: T) {
            bind(view, item)

            view.setOnClickListener {
                onClick?.invoke(item)
            }
            view.setOnLongClickListener {
                onLongClick?.invoke(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(data[position])
    }

    override fun getItemCount() = data.size

}

/*class AdapterRecyclerPN(private val data: ArrayList<PlayerItem>, val context: Context) : RecyclerView.Adapter<AdapterRecyclerPN.Recycler>() {

    inner class Recycler(Item: View) : RecyclerView.ViewHolder(Item) {
        var Player = Item.findViewById<TextView>(R.id.txt_TitleV)
        //var SPlayer = Item.findViewById<TextView>(R.id.txt_STitle)
        //var PlayTime = Item.findViewById<TextView>(R.id.txt_MGame)
        //var Price = Item.findViewById<TextView>(R.id.txt_PGame)
        //var Exit = Item.findViewById<CheckBox>(R.id.checkbox)

        @SuppressLint("NotifyDataSetChanged")
        fun bindData(position: Int) {
            val cur = data[position]
            Player.text = cur.Name
            //SPlayer.text = cur.Sub

            //PlayTime.text = cur.PlayedTimeH
            //Price.text = cur.PayedPrice.toString()
            //Exit.isChecked = cur.Attendance

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Recycler {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_playervertical,parent,false)
        return Recycler(layout)
    }
    override fun onBindViewHolder(holder: Recycler, position: Int) {
        holder.bindData(position)
    }
    override fun getItemCount(): Int {
        return data.size
    }
}*/