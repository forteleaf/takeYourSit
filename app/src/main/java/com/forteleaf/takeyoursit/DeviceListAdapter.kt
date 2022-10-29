package com.forteleaf.takeyoursit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class DeviceListAdapter :
    ListAdapter<Device, DeviceListAdapter.DeviceViewHolder>(DevicesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.address)
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            deviceItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): DeviceViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return DeviceViewHolder(view)
            }
        }
    }

    class DevicesComparator : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.name == newItem.name
        }
    }

}