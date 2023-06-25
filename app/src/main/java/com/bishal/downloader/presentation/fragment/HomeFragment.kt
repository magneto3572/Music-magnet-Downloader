package com.bishal.downloader.presentation.fragment

import RealPathUtil
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bishal.downloader.R
import com.bishal.downloader.databinding.FragmentHomeBinding
import com.bishal.downloader.domain.utils.Utils
import com.bishal.downloader.domain.utils.superNavigate
import com.bishal.downloader.presentation.basefragment.BaseFragment
import com.bishal.downloader.presentation.viewmodel.HomeViewModel
import com.bishal.ytdlplibrary.YoutubeDL.getInstance
import com.bishal.ytdlplibrary.mapper.VideoInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private var info : MutableStateFlow<VideoInfo?> = MutableStateFlow(null)
    private var url : String = String()
    private var outputPath : String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
        setupObserver()
    }

    private fun setupListener(){
        binding.apply {
            imageView3.setOnClickListener {
                editText.setText("")
            }

            folderSelectionView.setOnClickListener {
                openFileExplorer()
            }

            downloadBtn.setOnClickListener {
                if (!isStoragePermissionGranted()) {
                    Toast.makeText(
                        requireContext(),
                        "Please Grant Storage Permission and Retry",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }


                if(editText.text.isNotBlank()){
                    editText.isClickable = false
                    downloadBtn.text = "Grabbing Info.."
                    downloadBtn.setIconResource(R.drawable.icons)
                    getStreamInfo(editText.text.trim().toString())
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Url cannot be empty",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //get directory path
            grantUriPermission(result?.data?.data!!)
            outputPath = RealPathUtil.getRealPath(requireContext(), Utils.getDirectoryShortPath(result))
            binding.apply {
                folderSelectionView.text = outputPath.toString()
            }
        } else {
            Toast.makeText(requireContext(), "No directory selected", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun grantUriPermission(uri: Uri) {
        requireContext().applicationContext.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    private fun openFileExplorer() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        launcher.launch(intent)
    }

    private fun setupObserver(){
        lifecycleScope.launch{
            info.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED).collectLatest { info ->
                if (info != null){
                    val action =  HomeFragmentDirections.actionHomeFragmentToProgressFragment(info.fulltitle.toString(), info.likeCount.toString(), info.viewCount.toString(), info.thumbnail.toString(), url, info.fileSize, outputPath.toString(), info.title?.trim().toString())
                    superNavigate(action)
                }
            }
        }
    }

    private fun getStreamInfo(streamUrl: String) {
        runCatching {
            url = streamUrl
            lifecycleScope.launch(Dispatchers.IO) {
               val streamInfo =  async(Dispatchers.IO) {
                   getInstance().getInfo(url)
                }
                info.value = streamInfo.await()
            }
        }.getOrElse {
            it.printStackTrace()
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    }
}