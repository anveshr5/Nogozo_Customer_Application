package com.anvesh.nogozocustomerapplication.ui.main.customer.shops

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Item
import com.anvesh.nogozocustomerapplication.datamodels.Shop
import com.anvesh.nogozocustomerapplication.network.Database
import com.anvesh.nogozocustomerapplication.ui.main.customer.search.GlobalSearchFragment
import com.anvesh.nogozocustomerapplication.ui.main.customer.search.ItemInShopGlobalSearchAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ShopListAdapter(private val onShopClickInterface: OnShopClickInterface) :
    RecyclerView.Adapter<ShopListAdapter.ShopsViewHolder>() {

    private var originalList: List<Shop> = arrayListOf()
    private var filteredList: List<Shop> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopsViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_item_with_shop, parent, false)
        return ShopsViewHolder(v, onShopClickInterface)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ShopsViewHolder, position: Int) {
        holder.name.text = filteredList[position].shopName


        //GET SHOP ADDRESS
        if (filteredList[position].shopAddress == null && filteredList[position].shopId != "-1") {
            Database().getShopAddress(filteredList[position].shopId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        filteredList[position].shopAddress = snapshot.value as String
                        notifyDataSetChanged()
                    }
                })
        } else {
            holder.address.text = filteredList[position].shopAddress
        }
        //GET AREAID

        if (filteredList[position].deliveryStatus == null && filteredList[position].shopId != "-1") {
            Database().getDeliveryStatus(filteredList[position].shopId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        filteredList[position].deliveryStatus = snapshot.value as String
                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        if (filteredList[position].homebusiness == null && filteredList[position].shopId != "-1") {
            Database().getShopBusinessType(filteredList[position].shopId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null)
                            filteredList[position].homebusiness = snapshot.value as String?
                        else
                            filteredList[position].homebusiness = "No"
                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        } else {
            if (filteredList[position].homebusiness == "Yes") {
                holder.homeBusiness.visibility = View.VISIBLE
            } else {
                holder.homeBusiness.visibility = View.GONE
            }
        }

        val itemsListInShop: ArrayList<Item> = arrayListOf()
        if (GlobalSearchFragment.searchQuery != null) {
            //GET ITEMS IN SHOP
            val ref = Database().getItems(filteredList[position].shopId)

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        snapshot.children.forEach {
                            val item = it
                            val i = Item(item.key)
                            val v = item.value as HashMap<String, String>
                            i.itemName = v["itemname"]
                            i.itemPrice = v["itemprice"]!!
                            i.itemMRP = v["itemMRP"]
                            i.itemQuantity = v["quantity"]
                            i.isAvailable = v["isAvailable"] == "true"
                            i.itemImageUrl = v["itemimage"]
                            if (GlobalSearchFragment.searchQuery != null) {
                                if (i.itemName!!.contains(
                                        GlobalSearchFragment.searchQuery!!,
                                        true
                                    ) && !(itemsListInShop.contains(i))
                                )
                                    itemsListInShop.add(i)
                                //Log.d("snap", itemsListInShop.toString())
                            }
                        }
                    }
                    holder.recyclerViewItemsInShop.adapter = ItemInShopGlobalSearchAdapter(
                        itemsListInShop
                    )
                }
            })
        } else {
            itemsListInShop.clear()
            holder.recyclerViewItemsInShop.adapter?.notifyDataSetChanged()
        }

        //GET SHOP STATUS
        if (filteredList[position].shopCurrentStatus == null && filteredList[position].shopId != "-1") {
            Database().getShopStatus(filteredList[position].shopId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        filteredList[position].shopCurrentStatus = snapshot.value as String
                        notifyDataSetChanged()
                    }
                })
        } else {
            if (filteredList[position].shopCurrentStatus.equals("open", true))
                holder.available.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.green
                    )
                )
            else
                holder.available.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.red
                    )
                )
            holder.available.text = filteredList[position].shopCurrentStatus
        }
    }

    fun setItemList(dataList: ArrayList<Shop>) {
        this.filteredList = dataList
        this.originalList = dataList

        filteredList.forEach {
            Log.d("Datalist", it.shopName + "   ${it.deliveryStatus}    ${it.shopAreaId}")
        }
        notifyDataSetChanged()
        // SHOP ADDRESS
        val database = Database()
        CoroutineScope(Default).launch {
            for (i in 0 until originalList.size) {
                if (originalList[i].shopAddress == null && originalList[i].shopId != "-1") {
                    database.getShopAddress(originalList[i].shopId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                originalList[i].shopAddress = snapshot.value as String
                                notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                }
                //areaid
                if ((originalList[i].shopAreaId == "-1" || originalList[i].shopAreaId == "1" || originalList[i].shopAreaId == null) && originalList[i].shopId != "-1") {
                    database.getShopAreaId(originalList[i].shopId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                originalList[i].shopAreaId = snapshot.value as String
                                notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                }
                //shop status
                if (originalList[i].shopCurrentStatus == null && originalList[i].shopId != "-1") {
                    database.getShopStatus(originalList[i].shopId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                originalList[i].shopCurrentStatus = snapshot.value as String
                                notifyDataSetChanged()
                            }
                        })
                }

                if (originalList[i].deliveryStatus == null && originalList[i].shopId != "-1") {
                    database.getDeliveryStatus(originalList[i].shopId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                originalList[i].deliveryStatus = snapshot.value as String
                                notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                }

                if (originalList[i].homebusiness == null && originalList[i].shopId != "-1") {
                    database.getShopBusinessType(originalList[i].shopId)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.value != null) {
                                    originalList[i].homebusiness = snapshot.value as String
                                } else
                                    originalList[i].homebusiness = "No"
                                notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                }
                //sortLists()
            }
        }
    }

    fun sortLists() {
        filteredList = filteredList.sortedWith(compareBy<Shop>
        { it.shopAreaId}.thenBy { it.deliveryStatus }
            .thenByDescending { it.shopName })

        originalList = originalList.sortedWith(compareBy<Shop>
        { it.shopAreaId.equals(FirebaseAuth.getInstance().uid) }.thenBy { it.deliveryStatus }
            .thenByDescending { it.shopName })
    }
    fun removeAllItem() {
        this.filteredList = ArrayList()
        this.originalList = ArrayList()
        notifyDataSetChanged()
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val oReturn = FilterResults()
                val results: ArrayList<Shop> = ArrayList()
                if (constraint != null) {
                    val search = constraint.trim() as String
                    if (originalList.isNotEmpty()) {
                        for (g in originalList) {
                            if (g.shopName.toLowerCase(Locale.ROOT).contains(search))
                                results.add(g)
                        }
                    }
                    if (results.isEmpty()) {
                        results.add(Shop("No Shop Found", "-1", "", "", "-1",""))
                    }
                    oReturn.values = results
                }
                return oReturn
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredList = results.values as ArrayList<Shop>
                notifyDataSetChanged()
            }
        }
    }

    inner class ShopsViewHolder(
        itemView: View,
        private val onShopClickInterface: OnShopClickInterface
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var name: TextView = itemView.findViewById(R.id.list_item_shop_name)
        var address: TextView = itemView.findViewById(R.id.list_item_shop_address)
        var available: TextView = itemView.findViewById(R.id.list_item_shop_available)
        val recyclerViewItemsInShop: RecyclerView =
            itemView.findViewById(R.id.recyclerViewItemsInShop)
        val homeBusiness: TextView = itemView.findViewById(R.id.homeBusiness)

        override fun onClick(v: View?) {
            if (filteredList[adapterPosition].shopId == "-1")
                return
            if (filteredList[adapterPosition].shopAddress != null || filteredList[adapterPosition].shopId != "-1" || filteredList[adapterPosition].shopAreaId != "-1")
                onShopClickInterface.onShopClick(filteredList[adapterPosition])
            else
                Toast.makeText(
                    itemView.context,
                    "Please Wait For Data To Load...",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    interface OnShopClickInterface {
        fun onShopClick(shop: Shop)
    }
}