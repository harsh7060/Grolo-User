package com.example.blinkit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkit.R
import com.example.blinkit.databinding.ItemViewOrdersBinding
import com.example.blinkit.models.OrderedItems

class AdapterOrders(val context: Context, val onOrderItemViewClick: (OrderedItems) -> Unit): RecyclerView.Adapter<AdapterOrders.OrdersViewHolder>() {
    class OrdersViewHolder(val binding: ItemViewOrdersBinding): RecyclerView.ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>(){
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.binding.apply {
            tvOrderTitle.text = order.itemTitle
            tvOrderDate.text = order.itemDate
            tvOrderAmount.text = "₹${order.itemPrice.toString()}"
            when(order.itemStatus){
                0 ->{
                    tvOrderStatus.text = "Ordered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.yellow)
                }1 ->{
                    tvOrderStatus.text = "Received"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue)
                }2 ->{
                    tvOrderStatus.text = "Dispatched"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
                }3 ->{
                    tvOrderStatus.text = "Delivered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green)
                }
            }
        }
        holder.itemView.setOnClickListener{
            onOrderItemViewClick(order)
        }

    }
}