package com.example.kirimpesanapp.view.adapter

import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.databinding.ItemListHistoryBinding
import com.example.kirimpesanapp.view.DetailHistoryActivity

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemListHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val isNightMode = holder.itemView.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        if (isNightMode){
            holder.binding.apply {
                flHistory.setBackgroundResource(R.drawable.bg_card_horizontal_dark)
                ivClock.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.grey_light))
                tvDate.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.grey_light))
            }
        }

        holder.binding.apply {
            tvDate.text = "12/12/2021"
            tvTitle.text = "Pesan 1"
        }

        holder.itemView.setOnClickListener {
            startActivity(holder.itemView.context, Intent(holder.itemView.context, DetailHistoryActivity::class.java), null)
        }

    }

    override fun getItemCount(): Int {
        return 20
    }
}