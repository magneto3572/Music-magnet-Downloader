package com.bishal.downloader.presentation.fragment

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.transform.RoundedCornersTransformation
import com.arthenica.mobileffmpeg.FFmpeg
import com.bishal.downloader.R
import com.bishal.downloader.databinding.FragmentProgressBinding
import com.bishal.downloader.domain.utils.Load
import com.bishal.downloader.domain.utils.Utils
import com.bishal.downloader.presentation.basefragment.BaseFragment
import com.bishal.downloader.presentation.viewmodel.ProgressViewModel
import com.bishal.ytdlplibrary.YoutubeDL
import com.bishal.ytdlplibrary.YoutubeDLRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


@AndroidEntryPoint
class ProgressFragment : BaseFragment<FragmentProgressBinding, ProgressViewModel>() {

    private var job : Job? = null
    private var processId : Long? = null
    private val args : ProgressFragmentArgs by navArgs()
    private val tempStorage =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupView()
        getUrlAndRequestFromLib()
        setupListener()
    }

    private fun setupListener(){
        binding.apply {
            materialButton3.setOnClickListener {
                kotlin.runCatching {
                    YoutubeDL.destroyProcessById(processId.toString())
                    job?.cancel("Closing running job")
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupView() {
        binding.apply {
            thumbnail.Load(requireContext(), args.thumbnail,RoundedCornersTransformation())
            textView4.text = args.title
            viewcount.text = args.viewcount
            likecount.text = args.likecount
        }
    }


    private fun getUrlAndRequestFromLib() {
        runCatching {
            job = lifecycleScope.launch(Dispatchers.IO){
                val youtubeDLDir = File(
                    tempStorage,
                    "PawxyDownloader"
                )
                val request = YoutubeDLRequest(args.url)
                request.addOption("-o", youtubeDLDir.absolutePath + "/%(id)s.%(ext)s")
                request.addOption("--no-mtime");
                request.addOption("--downloader", "libaria2c.so")
                request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
                YoutubeDL.getInstance().execute(request, "MyDlProcess") { progress, o2, line ->
                    lifecycleScope.launch(Dispatchers.Main){
                        binding.apply {
                            progressData.text = line.toString()
                            progressBar.progress = progress.toInt()
                        }
                    }
                }
                withContext(Dispatchers.Main){
                    delay(2000)
                    binding.progressBar.progress = 100
                    binding.textView5.text = "Converting.."
                    binding.progressBar.setIndicatorColor(resources.getColor(R.color.indicator, null))
                    convertToMp3(
                        "$tempStorage/PawxyDownloader/"+args.id,
                        args.outputpath
                    )
                    delay(500)
                    binding.textView5.text = "Saving.."
                    binding.progressBar.setIndicatorColor(resources.getColor(R.color.savingcolor, null))
                    delay(500)
                    binding.textView5.text = "Success.."
                    binding.progressBar.setIndicatorColor(resources.getColor(R.color.successcolor, null))
                    binding.materialButton3.visibility = View.VISIBLE
                }
            }
        }.getOrElse {
            it.printStackTrace()
            Log.d("LogTagStackTrace", it.message.toString())
            Toast.makeText(requireContext(), "Something Went Wrong..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToMp3(inputFilePath: String, outputFilePath: String) {
        val file = Utils.getConvertedFile(outputFilePath, args.id+".mp3")
        lifecycleScope.launch(Dispatchers.IO) {
            val command = arrayOf(
                "-i",
                "$inputFilePath.mp4",
                "-vn",
                "-c:a",
                "libmp3lame",
                "-qscale:a",
                "0",
                file.path
            )
            try {
                FFmpeg.executeAsync(command) { executionId, returnCode ->
                    processId =  executionId
                    if (returnCode == 1){
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                Log.w("LogTagError",e)
                e.printStackTrace()
            }
        }
    }
}