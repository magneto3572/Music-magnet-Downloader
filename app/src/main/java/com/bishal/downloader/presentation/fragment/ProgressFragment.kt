package com.bishal.downloader.presentation.fragment

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.transform.RoundedCornersTransformation
import com.arthenica.mobileffmpeg.FFmpeg
import com.bishal.downloader.databinding.FragmentProgressBinding
import com.bishal.downloader.domain.utils.Load
import com.bishal.downloader.presentation.basefragment.BaseFragment
import com.bishal.downloader.presentation.viewmodel.ProgressViewModel
import com.bishal.ytdlplibrary.YoutubeDL
import com.bishal.ytdlplibrary.YoutubeDLRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


@AndroidEntryPoint
class ProgressFragment : BaseFragment<FragmentProgressBinding, ProgressViewModel>() {

    private val args : ProgressFragmentArgs by navArgs()
    private val tempStorage =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        getUrlAndRequestFromLib()
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
            lifecycleScope.launch(Dispatchers.IO){
                val youtubeDLDir = File(
                    tempStorage,
                    "PawxyDownloader"
                )
                val request = YoutubeDLRequest(args.url)
                request.addOption("-o", youtubeDLDir.absolutePath + "/%(title)s.%(ext)s")
                request.addOption("--no-mtime");
                request.addOption("--downloader", "libaria2c.so")
                request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"")
                YoutubeDL.getInstance().execute(request, "MyDlProcess") { progress, o2, line ->
                    lifecycleScope.launch(Dispatchers.Main){
                        Log.d("LogTagProgress", progress.toInt().toString())
                        Log.d("LogTagProgress2", o2.toString())
                        Log.d("LogTagProgress3", line.toString())
                        binding.apply {
                            progressData.text = line.toString()
                            if (progress.toInt() != -1){
                                if (progress.toInt() >= 98){
                                    progressBar.progress = 100
                                }else{
                                    progressBar.progress = progress.toInt()
                                }
                            }else{
                                progressBar.progress = 100
                            }

                        }
                        if (line.contains("100%") && progress.toInt()>=80){
                            convertToMp3("$tempStorage/PawxyDownloader",
                                "$tempStorage/PawxyDownloader"
                            )
                        }
                    }
                }
            }
        }.getOrElse {
            it.printStackTrace()
            Toast.makeText(requireContext(), "Something Went Wrong..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToMp3(inputFilePath: String, outputFilePath: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val command = arrayOf(
                "-i",
                inputFilePath,
                "-vn",
                "-c:a",
                "libmp3lame",
                "-qscale:a",
                "0",
                outputFilePath+args.title+".mp3"
            )
            try {
                val s = FFmpeg.execute(command)
                Log.d("LogTagMp3", s.toString())
                Log.d("LogTagMp3", outputFilePath.toString())
                moveFileToDestination(outputFilePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun moveFileToDestination(sourceFilePath: String) {
        try {
            val sourceFile = File(sourceFilePath)
            var destinationFile: File


            // setup the final file extension
//            val fileExtension = if (metadata?.mediaFormat == true)
//                "mp4"
//            else "mp3"

            val destinationFullPath = tempStorage + "PawxyDownloader" + "/${args.title}.mp3"

            //create a new file at destination path with the file name
            destinationFile = File(destinationFullPath)

            //add suffix if the file name exists
            var i = 1;
            while (destinationFile.exists()) {
                destinationFile =
                    File(tempStorage + "PawxyDownloader" + "/${args.title}.mp3")
                i++
            }
            if (sourceFile.exists()) {
                val inputStream = FileInputStream(sourceFile)
                val outputStream = FileOutputStream(destinationFile)

                val bufferSize = 8 * 1024 // 8 KB buffer size
                val buffer = ByteArray(bufferSize)
                var bytesRead: Int
                var totalBytesRead: Long = 0
                val fileSize = sourceFile.length()

                while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead

                    // progress of file transfer
//                    _progressSlider.value = totalBytesRead.toFloat() / fileSize * 100
                    Log.d(
                        "LogTagTransferProgress",
                        (totalBytesRead.toFloat() / fileSize * 100).toString()
                    )
                }

                inputStream.close()
                outputStream.close()
            } else {

            }
        } catch (e: IOException) {
            // Handle any IO exceptions
            e.printStackTrace()
        }


        //delete all the temporary files
        //freeUpResources()
    }
}