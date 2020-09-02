package com.anvesh.nogozocustomerapplication.ui.userdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.TextView
import com.anvesh.nogozocustomerapplication.R
import com.anvesh.nogozocustomerapplication.datamodels.Area
import java.util.*
import kotlin.collections.ArrayList

class AreaListAdapter:BaseAdapter() {

    private var OriginalList: List<Area> = ArrayList()
    private var filteredList: List<Area> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View
        var holder = ViewHolder()

        return if(convertView == null){
            v = LayoutInflater.from(parent!!.context).inflate(R.layout.list_item_city, parent, false)
            holder.name = v.findViewById(R.id.list_item_city_view)
            v.tag = holder
            holder.name.text = filteredList[position].areaName
            v
        }else{
            holder = convertView.tag as ViewHolder
            holder.name.text = filteredList[position].areaName
            convertView
        }
    }

    override fun getItem(position: Int): Area {
        return filteredList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return filteredList.size
    }

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults? {
                val oReturn = FilterResults()
                val results: ArrayList<Area> = ArrayList()
                if (constraint != null) {
                    if (OriginalList.isNotEmpty()) {
                        for (g in OriginalList) {
                            if (g.areaName.toLowerCase(Locale.ROOT).contains(constraint.toString()))
                                results.add(g)
                        }
                    }
                    if(results.isEmpty()){
                        results.add(Area("Looks Like We Are Not In That Area", "-1"))
                    }
                    oReturn.values = results
                }
                return oReturn
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults){
                filteredList = results.values as ArrayList<Area>
                notifyDataSetChanged()
            }
        }
    }

    fun setOriginalList(data: List<Area>){
        this.OriginalList = data
        this.filteredList = data
        notifyDataSetChanged()
    }

    class ViewHolder{
        lateinit var name: TextView
    }
}