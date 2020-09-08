package com.anvesh.nogozocustomerapplication.ui.main.customer.itemsInShop

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Item
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ItemsInShopAdapter() : RecyclerView.Adapter<ItemsInShopAdapter.ItemsViewHolder>() {

    private var originalList: List<Item> = ArrayList()
    private var filteredList: List<Item> = ArrayList()
    private var selectedItem: HashMap<String, Int> = HashMap() // scheme = {itemid: times}
    private var priceLiveData: MediatorLiveData<Int> = MediatorLiveData()
    private var acceptOrders: Boolean = false
    private val itemImageBaseUrl = FirebaseStorage.getInstance().reference.child("items")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_iteminshop, parent, false)
        return ItemsViewHolder(v)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.itemName.text = filteredList[position].itemName
        holder.itemPrice.text = "₹${filteredList[position].itemPrice}"

        if (acceptOrders && filteredList[position].isAvailable!!)
            holder.wrapper.visibility = View.VISIBLE
        else
            holder.wrapper.visibility = View.INVISIBLE

        holder.itemMRP.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG // Striking MRP

        if (filteredList[position].itemMRP == null || filteredList[position].itemMRP == filteredList[position].itemPrice) {
            holder.itemMRP.visibility = View.INVISIBLE
        } else {
            holder.itemMRP.visibility = View.VISIBLE
            holder.itemMRP.text = "₹${filteredList[position].itemMRP}"
        }

        holder.itemDesc.text = filteredList[position].itemQuantity
        if (selectedItem.containsKey(filteredList[position].itemId)) {
            holder.itemQuantity.text = "${selectedItem[filteredList[position].itemId!!]}"
        } else {
            holder.itemQuantity.text = "0"
        }

        Glide.with(holder.itemView.context)
            .load(itemImageBaseUrl.child(filteredList[position].itemId!!))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(holder.itemImage)

        if (!filteredList[position].isAvailable!!) {
            holder.itemName.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey
                )
            )
            holder.itemPrice.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey
                )
            )
            holder.itemMRP.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey
                )
            )
            holder.itemDesc.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey
                )
            )
            holder.itemQuantity.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey
                )
            )
        } else {
            holder.itemName.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
            holder.itemPrice.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
            holder.itemMRP.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
            holder.itemDesc.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
            holder.itemQuantity.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
        }
    }

    fun setData(dataList: List<Item>,shopStatus: Boolean) {
        acceptOrders = shopStatus
        this.filteredList = dataList.sortedWith(compareBy<Item>{it.itemGroup}.thenBy{it.itemName})
        this.originalList = dataList.sortedWith(compareBy<Item>{it.itemGroup}.thenBy{it.itemName})
        notifyDataSetChanged()
    }

    fun getPriceLiveData(): LiveData<Int> {
        return priceLiveData
    }

    fun calculateTotal() {
        CoroutineScope(Dispatchers.Default).launch {
            var price = 0
            for ((key, value) in selectedItem) {
                val posi = idToPosition(key)
                if (posi != -1)
                    price += originalList[posi].itemPrice.toInt() * value
            }
            println(price)
            priceLiveData.postValue(price)
        }
    }

    suspend fun idToPosition(itemId: String): Int {
        for (i in 0 until originalList.size) {
            if (originalList[i].itemId == itemId) {
                return i
            }
        }
        return -1
    }

    suspend fun getSelectedItem(): HashMap<String, Any> {
        val map: HashMap<String, Any> = HashMap()
        for ((key, value) in selectedItem) {
            val itemMap: HashMap<String, String> = HashMap()
            val posi = idToPosition(key)
            itemMap["itemname"] = originalList[posi].itemName!!
            itemMap["quantity"] = originalList[posi].itemQuantity!!
            itemMap["times"] = value.toString()
            map[originalList[posi].itemId!!] = itemMap
        }
        return map
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val oReturn = FilterResults()
                val results: ArrayList<Item> = ArrayList()
                if (constraint != null) {
                    val search = constraint.trim() as String
                    if (originalList.isNotEmpty()) {
                        for (g in originalList) {
                            if (g.itemName!!.toLowerCase(Locale.ROOT).contains(search))
                                results.add(g)
                        }
                    }
                    if (results.isEmpty()) {
                        results.add(Item("-1", "No item found", isAvailable = false))
                    }
                    oReturn.values = results
                }
                return oReturn
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredList = results.values as ArrayList<Item>
                notifyDataSetChanged()
            }
        }
    }

    inner class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var itemImage: ImageView = itemView.findViewById(R.id.list_item_iteminshop_image)
        var itemName: TextView = itemView.findViewById(R.id.list_item_iteminshop_name)
        var itemPrice: TextView = itemView.findViewById(R.id.list_item_iteminshop_price)
        var itemMRP: TextView =
            itemView.findViewById(R.id.list_item_iteminshop_mrp) // MRP TextViewHolder
        var itemDesc: TextView = itemView.findViewById(R.id.list_item_iteminshop_desc)
        var itemQuantity: TextView = itemView.findViewById(R.id.list_item_iteminshop_quantity)
        var wrapper: View = itemView.findViewById(R.id.action_wrapper)
        private val minus: ImageButton = itemView.findViewById(R.id.list_item_iteminshop_minus)
        private val add: ImageButton = itemView.findViewById(R.id.list_item_iteminshop_add)

        init {
            minus.setOnClickListener(this)
            add.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val itemId = filteredList[adapterPosition].itemId!!
            when (v!!.id) {
                R.id.list_item_iteminshop_add -> {
                    if (selectedItem.containsKey(itemId)) {
                        selectedItem[itemId] = selectedItem[itemId]!! + 1
                    } else {
                        selectedItem[itemId] = 1
                    }
                    notifyDataSetChanged()
                    calculateTotal()
                }
                R.id.list_item_iteminshop_minus -> {
                    if (selectedItem.containsKey(itemId)) {
                        val a = selectedItem[itemId]!! - 1
                        if (a == 0) {
                            selectedItem.remove(itemId)
                        } else {
                            selectedItem[itemId] = a
                        }
                        notifyDataSetChanged()
                        calculateTotal()
                    }
                }
            }
        }
    }
}