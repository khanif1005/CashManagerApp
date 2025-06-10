package com.example.cashmanagerapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cashmanagerapplication.databinding.AdapterAvatarBinding

class AvatarAdapter(var avatars: ArrayList<Int>,
                    var listener: AdapterListener
): RecyclerView.Adapter<AvatarAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarAdapter.ViewHolder {
        return ViewHolder(
            AdapterAvatarBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AvatarAdapter.ViewHolder, position: Int) {
        val avatar: Int = avatars[position]
        holder.binding.imgRravatar.setImageResource( avatar )
        holder.binding.imgRravatar.setOnClickListener {
            listener.onClick(avatar)
        }
    }

    //perhatikan ini
    override fun getItemCount(): Int {
        return avatars.size
    }

    class ViewHolder(val binding: AdapterAvatarBinding): RecyclerView.ViewHolder(binding.root)

    interface AdapterListener{
        fun onClick(avatar: Int)
    }
}