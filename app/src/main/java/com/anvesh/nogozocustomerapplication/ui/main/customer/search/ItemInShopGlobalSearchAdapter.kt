package com.anvesh.nogozocustomerapplication.ui.main.customer.search

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Item

class ItemInShopGlobalSearchAdapter(
    val itemList: ArrayList<Item>): RecyclerView.Adapter<ItemInShopGlobalSearchAdapter.ItemInShopGlobalViewHolder>() {

    private val itemImageBaseUrl = FirebaseStorage.getInstance().reference.child("items")

    class ItemInShopGlobalViewHolder(view: View,parent: ViewGroup): RecyclerView.ViewHolder(view){
        val itemName: TextView = view.findViewById(R.id.list_item_iteminshop_name)
        val itemDesc :TextView = view.findViewById(R.id.list_item_iteminshop_desc)
        val itemPrice: TextView = view.findViewById(R.id.list_item_iteminshop_price)
        val itemMRP:TextView = view.findViewById(R.id.list_item_iteminshop_mrp)
        val actionWrapper: LinearLayout= view.findViewById(R.id.action_wrapper)
        val itemImage : ImageView = view.findViewById(R.id.list_item_iteminshop_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemInShopGlobalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_iteminshop,parent,false)

        return ItemInShopGlobalViewHolder(view, parent)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemInShopGlobalViewHolder, position: Int) {
        val item = itemList[position]

        holder.itemName.text = item.itemName
        holder.itemDesc.text = item.itemQuantity
        holder.itemPrice.text = "₹${item.itemPrice}"
        holder.itemMRP.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        if (item.itemMRP == null || item.itemMRP == item.itemPrice){
            holder.itemMRP.visibility = View.INVISIBLE
        } else {
            holder.itemMRP.visibility = View.VISIBLE
            holder.itemMRP.text = "₹${item.itemMRP}"
        }

        Glide.with(holder.itemView.context)
            .load(itemImageBaseUrl.child(itemList[position].itemId!!))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(holder.itemImage)

        holder.actionWrapper.visibility = View.GONE
    }
}