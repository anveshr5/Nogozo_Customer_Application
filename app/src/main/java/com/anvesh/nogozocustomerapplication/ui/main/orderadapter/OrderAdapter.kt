package com.anvesh.nogozocustomerapplication.ui.main.orderadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Order
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.util.OrderByDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderAdapter(
    private val comparator: Comparator<Order> = OrderByDateTime()
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orderList: ArrayList<Order> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_order, parent, false)
        return OrderViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.shopName.text = orderList[position].shopname
        holder.dateTime.text = "${orderList[position].date} on ${orderList[position].time}"
        if (orderList[position].deliverycharges != "0")
            holder.price.text =
                "₹${orderList[position].itemprice} + ₹${orderList[position].deliverycharges}"
        else
            holder.price.text = "₹${orderList[position].itemprice}"

        holder.instruction.text = orderList[position].shopinstruction

        holder.orderId.text = orderList[position].orderId

        if (orderList[position].shopinstruction.isNullOrBlank())
            holder.instruction.visibility = View.GONE

        //items stored in map under each order
        var items = ""
        for ((key, values) in orderList[position].items) {
            val item = values as HashMap<String, String>
            items += "${item["times"]} x ${item["itemname"]}(${item["quantity"]})\n"
        }
        items = items.removeSuffix("\n")
        holder.items.text = items

        if (orderList[position].delivery == "Yes") {
            when (orderList[position].status) {
                "0" -> holder.status.text = "Packing Your Order"
                "1" -> holder.status.text = "Delivery Executive reaching to Shop"
                "2" -> holder.status.text = "Out For Delivery"
                "3" -> holder.status.text = "Delivered"
                "-1"-> holder.status.text = "Cancelled"
            }
        } else {
            when (orderList[position].status) {
                "0" -> holder.status.text = "Packing Your Order"
                "1" -> holder.status.text = "Order ready to pick up at Shop"
                "2" -> holder.status.text = "Order is ready to pick up"
                "3" -> holder.status.text = "Picked Up"
                "-1"-> holder.status.text = "Cancelled"
            }
        }
    }

    fun setList(dataList: ArrayList<Order>) {
        orderList = dataList
        CoroutineScope(Dispatchers.Default).launch {
            orderList.sortWith(comparator)
            withContext(Main) {
                notifyDataSetChanged()
            }
        }
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val orderId: TextView = itemView.findViewById(R.id.list_item_current_order_id)
        val shopName: TextView = itemView.findViewById(R.id.list_item_current_order_shopname)
        val dateTime: TextView = itemView.findViewById(R.id.list_item_current_order_date_time)
        val price: TextView = itemView.findViewById(R.id.list_item_current_order_price)
        val items: TextView = itemView.findViewById(R.id.list_item_current_order_items)
        val status: TextView = itemView.findViewById(R.id.list_item_current_order_status)
        val instruction: TextView = itemView.findViewById(R.id.list_item_current_order_instruction)

        override fun onClick(v: View?) {

        }
    }
}