package com.hashim.overlayserviceapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hashim.overlayserviceapp.databinding.ItemAppLockListBinding


class AppLockItemViewHolder(
    private var binding: ItemAppLockListBinding,
) : RecyclerView.ViewHolder(binding.root) {

    init {

    }

    fun bind(appLockItemViewState: AppData) {
        binding.imageViewAppIcon.setImageDrawable(appLockItemViewState.appIconDrawable)
        binding.hAppName.text = appLockItemViewState.appName
    }

    companion object {
        fun create(
            parent: ViewGroup,
        ): AppLockItemViewHolder {
            val binding = ItemAppLockListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return AppLockItemViewHolder(binding)
        }
    }
}