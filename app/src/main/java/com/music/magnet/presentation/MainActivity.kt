package com.music.magnet.presentation

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.music.magnet.BuildConfig
import com.music.magnet.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        requestPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestPermission(){
        if (!isExternalStorageManager(BuildConfig.APPLICATION_ID, this, this.packageManager)){
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun isExternalStorageManager(
        packageName: String,
        context: Context,
        packageManger: PackageManager
    ): Boolean {
        return hasPermission(packageName,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE, context, packageManger)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun hasPermission(
        packageName: String,
        permission: String,
        context: Context,
        packageManger: PackageManager
    ): Boolean {
        val granted: Boolean
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

        val info = packageManger.getApplicationInfo(packageName, 0)
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.permissionToOp(permission)!!,
            info.uid,
            packageName
        )
        granted = if (mode == AppOpsManager.MODE_DEFAULT) {
            context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            (mode == AppOpsManager.MODE_ALLOWED)
        }

        return granted
    }
}