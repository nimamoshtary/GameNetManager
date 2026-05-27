package ir.lifeplus.gamenethelper.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

//A generic adapter for any task!
//It requires:
//A data list of type T
//The ID of the desired item layout
//A bind function that fills the layout view
//An OnClick function to perform an action when any part of the item is clicked
//An OnLongClick function to perform an action when the item is long‑clicked
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