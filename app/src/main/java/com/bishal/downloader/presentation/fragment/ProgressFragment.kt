package com.bishal.downloader.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bishal.downloader.databinding.FragmentProgressBinding
import com.bishal.downloader.presentation.basefragment.BaseFragment
import com.bishal.downloader.presentation.viewmodel.ProgressViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProgressFragment : BaseFragment<FragmentProgressBinding, ProgressViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}