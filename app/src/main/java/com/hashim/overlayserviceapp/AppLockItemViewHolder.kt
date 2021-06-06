package com.hashim.overlayserviceapp

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hashim.overlayserviceapp.databinding.ItemAppLockListBinding


class AppLockItemViewHolder(
    private var binding: ItemAppLockListBinding,
    private val hClickCallBack: (appdata: AppData) -> Unit,
    private val hContext: Context
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(appData: AppData) {
        binding.imageViewLock.setOnClickListener {
            hClickCallBack(appData)
        }

        val hpref = PreferenceManager.getDefaultSharedPreferences(hContext)
        val string = hpref.getString("AppName", null)
        string?.let {
            if (it.equals(appData.appName))
                binding.imageViewLock.setImageDrawable(ContextCompat.getDrawable(hContext, R.drawable.ic_lock))
        }

        binding.imageViewAppIcon.setImageDrawable(appData.appIconDrawable)
        binding.hAppName.text = appData.appName
    }

    companion object {
        fun create(
            parent: ViewGroup,
            hClickCallBack: (appdata: AppData) -> Unit,
            hContext: Context
        ): AppLockItemViewHolder {
            val binding = ItemAppLockListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return AppLockItemViewHolder(binding, hClickCallBack, hContext)
        }
    }
}