package com.hashim.overlayserviceapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.hashim.overlayserviceapp.databinding.DialogUsagePermissionBinding


class UsageAccessPermissionDialog : DialogFragment() {

    lateinit var binding: DialogUsagePermissionBinding
    private lateinit var windowManager: WindowManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUsagePermissionBinding.inflate(layoutInflater, container, false)
        binding.buttonPermit.setOnClickListener {
            onPermitClicked()
        }
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun onPermitClicked() {
        try {
            startActivity(IntentHelper.usageAccessIntent())

            dismiss()
        } catch (e: Exception) {
        }
    }

    companion object {

        fun newInstance() = UsageAccessPermissionDialog()
    }


}