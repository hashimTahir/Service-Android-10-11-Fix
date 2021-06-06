package com.hashim.overlayserviceapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class AppsAdapter(
    val hContext: Context,
    val hClickCallBack: (appdata: AppData) -> Unit,
) : RecyclerView.Adapter<AppLockItemViewHolder>() {

    var hList = mutableListOf<AppData>()

    @SuppressLint("CheckResult")
    fun setAppDataList(itemViewStateList: List<AppData>) {
        hList = itemViewStateList as MutableList<AppData>
    }

    override fun getItemCount(): Int = hList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppLockItemViewHolder {
        return AppLockItemViewHolder.create(parent, hClickCallBack, hContext)
    }


    override fun onBindViewHolder(holder: AppLockItemViewHolder, position: Int) {
        holder.bind(hList[position])
    }

}