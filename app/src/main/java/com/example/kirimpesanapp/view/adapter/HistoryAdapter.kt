package com.example.kirimpesanapp.view.adapter

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.kirimpesanapp.R
import com.example.kirimpesanapp.data.model.DataRecognition
import com.example.kirimpesanapp.databinding.ItemListHistoryBinding
import com.example.kirimpesanapp.view.DetailHistoryActivity
import com.example.kirimpesanapp.viewmodel.DataRecognitionViewModel

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private var dataRecognition = emptyList<DataRecognition>()
    private lateinit var viewModel: DataRecognitionViewModel

    class ViewHolder(var binding: ItemListHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewModel =
            ViewModelProvider(holder.itemView.context as FragmentActivity)[DataRecognitionViewModel::class.java]
        val currentItem = dataRecognition[position]
        val isNightMode =
            holder.itemView.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val image = currentItem.imageUri
        val bitmap = MediaStore.Images.Media.getBitmap(
            holder.itemView.context.contentResolver,
            Uri.parse(image)
        )

        if (isNightMode) {
            holder.binding.apply {
                flHistory.setBackgroundResource(R.drawable.bg_card_horizontal_dark)
                ivClock.setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey_light
                    )
                )
                tvDateHistory.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey_light
                    )
                )
            }
        }

        holder.binding.apply {
            tvDateHistory.text = currentItem.dateRecognition
            tvTitleHistory.text = currentItem.phoneNumber
            ivHistory.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailHistoryActivity::class.java)
            intent.putExtra("id", currentItem.id)
            intent.putExtra("phoneNumber", currentItem.phoneNumber)
            intent.putExtra("dateRecognition", currentItem.dateRecognition)
            intent.putExtra("image", currentItem.imageUri)
            startActivity(holder.itemView.context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return dataRecognition.size
    }

    @Suppress("NotifyDataSetChanged")
    fun setData(shortcut: List<DataRecognition>) {
        this.dataRecognition = shortcut.sortedByDescending { it.dateRecognition }
        notifyDataSetChanged()
    }
}