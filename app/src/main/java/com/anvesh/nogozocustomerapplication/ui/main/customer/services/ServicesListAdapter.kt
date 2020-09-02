package com.anvesh.nogozocustomerapplication.ui.main.customer.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Services
import com.anvesh.nogozocustomerapplication.ui.main.customer.search.GlobalSearchFragment

class ServicesListAdapter( private val onServiceClickInterface: OnServicesClickInterface): RecyclerView.Adapter<ServicesListAdapter.ServicesViewHolder>() {

    private var dataList: List<Services> = ArrayList()
    private var storage = FirebaseStorage.getInstance().reference.child("services")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_service, parent, false)
        return ServicesViewHolder(v, onServiceClickInterface)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ServicesViewHolder, position: Int) {
        holder.textView.text = dataList[position].servicename
        Glide.with(holder.itemView.context)
            .load(storage.child(dataList[position].serviceId!!))
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(holder.imageView)
    }

    fun setData(dataList: List<Services>){
        this.dataList = dataList
        notifyDataSetChanged()
        GlobalSearchFragment.searchQuery = null
    }

    fun getItemAt(position: Int): Services{
        return dataList[position]
    }

    class ServicesViewHolder(itemView: View, private val onServicesClickInterface: OnServicesClickInterface) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        init {
            itemView.setOnClickListener(this)
        }

        var imageView: ImageView = itemView.findViewById(R.id.list_item_service_imageview)
        var textView: TextView = itemView.findViewById(R.id.list_item_service_text)

        override fun onClick(v: View?) {
            onServicesClickInterface.onServiceClick(adapterPosition)
        }
    }

    interface OnServicesClickInterface{
        fun onServiceClick(position: Int)
    }
}