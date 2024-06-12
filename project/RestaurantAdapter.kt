package com.example.project.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.model.Restaurant

class RestaurantAdapter(
    private var restaurants: List<Restaurant>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    fun updateData(newList: List<Restaurant>) {
        restaurants = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return RestaurantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentRestaurant = restaurants[position]

        // 식당 이름 설정
        val nameText = currentRestaurant.name

        // 혼잡도 텍스트 설정
        val congestionText = " (혼잡도: ${currentRestaurant.congestion}%)"

        // 혼잡도에 따라 텍스트 색상 설정
        val textColor = when {
            currentRestaurant.congestion < 40 -> Color.GREEN
            currentRestaurant.congestion < 60 -> Color.BLUE
            else -> Color.RED
        }

        // 혼잡도 텍스트에만 색상 설정
        val congestionSpannable = SpannableString(congestionText)
        congestionSpannable.setSpan(ForegroundColorSpan(textColor), 0, congestionText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 텍스트 뷰에 이름과 혼잡도 텍스트 설정
        holder.textViewName.text = SpannableStringBuilder().append(nameText).append(congestionSpannable)

        holder.itemView.setOnClickListener {
            listener.onItemClick(position, currentRestaurant.menu)
        }
    }

    override fun getItemCount() = restaurants.size

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        // 이미지 뷰 추가
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, menu: List<String>)
    }
}
