package com.bishal.downloader.presentation.fragment

import android.os.Bundle
import android.view.View
import com.bishal.downloader.databinding.FragmentHomeBinding
import com.bishal.downloader.presentation.basefragment.BaseFragment
import com.bishal.downloader.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
    }

    private fun setupListener(){
        binding.apply {
            downloadBtn.setOnClickListener {

                if(editText.text.isNotBlank()){
                    getUrlAndRequestFromLib(editText.text.trim().toString())
                    editText.setText("")
                }
            }
        }
    }

    private fun getUrlAndRequestFromLib(url: String) {


    }
}