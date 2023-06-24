package com.bishal.downloader.presentation.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.bishal.downloader.databinding.FragmentHomeBinding
import com.bishal.downloader.presentation.basefragment.BaseFragment
import com.bishal.downloader.presentation.viewmodel.HomeViewModel
import com.bishal.ffmpeg.FFmpeg
import com.bishal.ytdlplibrary.YoutubeDL.getInstance
import com.bishal.ytdlplibrary.YoutubeDLException
import com.bishal.ytdlplibrary.YoutubeDLRequest
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }

    private fun setupListener(){
        binding.apply {
            downloadBtn.setOnClickListener {

                if (!isStoragePermissionGranted()) {
                    Toast.makeText(
                        requireContext(),
                        "Please Grant Storage Permission and Retry",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                getUrlAndRequestFromLib("https://vimeo.com/22439234")

                if(editText.text.isNotBlank()){

                }
            }
        }
    }

    private fun getUrlAndRequestFromLib(url: String) {
        Toast.makeText(requireContext(), "triggered", Toast.LENGTH_SHORT).show()
        val youtubeDLDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "youtubedl-android"
        )
        val request = YoutubeDLRequest(url)
        request.addOption("-o", youtubeDLDir.absolutePath + "/%(title)s.%(ext)s")
        val streamInfo = getInstance().getInfo(url)
        Log.d("LogTagStreamInfo", streamInfo.title.toString())
        getInstance().execute(request, "MyDlProcess", callback)
    }

    private val callback: (Float, Long?, String?) -> Unit = { progress: Float, o2: Long?, line: String? ->
        Log.d("LogTagProgress", progress.toString())
        Log.d("LogTagProgress2", o2.toString())
        Log.d("LogTagProgress3", line.toString())
    }

    private fun getDownloadLocation(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val youtubeDLDir = File(downloadsDir, "youtubedl-android")
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir()
        return youtubeDLDir
    }

    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PermissionChecker.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            true
        }
    }
}