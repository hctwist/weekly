package com.twisthenry8gmail.weeklyphoenix.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.twisthenry8gmail.weeklyphoenix.R
import com.twisthenry8gmail.weeklyphoenix.databinding.FragmentMainMenuBinding

class FragmentMainMenu: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainMenuBinding.inflate(inflater, container, false)

        binding.mainMenuMenu.setCheckedItem(R.id.main_home)
        return binding.root
    }
}