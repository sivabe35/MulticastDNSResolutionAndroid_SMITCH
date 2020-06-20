package com.siva.multicastdnsresolutionandroid.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.siva.multicastdnsresolutionandroid.R
import com.siva.multicastdnsresolutionandroid.databinding.NsdServiceInfoItemBinding
import com.siva.multicastdnsresolutionandroid.model.NSDServiceInfo
import kotlinx.android.synthetic.main.nsd_service_info_item.view.*
import java.util.*

class NsdServiceListAdapter(val context: Context?, private var serviceInfoList: ArrayList<NSDServiceInfo>) :
    RecyclerView.Adapter<NsdServiceListAdapter.ViewHolder>() {


    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        DataBindingUtil.inflate<NsdServiceInfoItemBinding>(
            LayoutInflater.from(context), R.layout.nsd_service_info_item, parent, false).root) {
        @SuppressLint("SetTextI18n")
        fun bind(serviceInfoItem: NSDServiceInfo) {

            itemView.tvServiceName.text = "Service Name : ${serviceInfoItem.serviceName}"
            itemView.tvServicType.text = "Service Type : ${serviceInfoItem.serviceType}"
            itemView.tvIp.text = "Ip Address : ${serviceInfoItem.host?.hostAddress}"
            itemView.tvPort.text = "Port : ${serviceInfoItem.port}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return serviceInfoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(serviceInfoList[position])
    }

    fun addNsdServiceItem(item: NSDServiceInfo) {
        serviceInfoList.add(item)
        notifyDataSetChanged()
    }
}