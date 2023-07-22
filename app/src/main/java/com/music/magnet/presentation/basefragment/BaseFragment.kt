package com.music.magnet.presentation.basefragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.MaterialFadeThrough
import java.lang.reflect.ParameterizedType

abstract class BaseFragment <VB : ViewBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: VB private set
    protected lateinit var viewModel: VM private set
    protected lateinit var window: Window

    private val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVB = type.actualTypeArguments[0] as Class<VB>
    private val classVM = type.actualTypeArguments[1] as Class<VM>
    var token : String? = null


    private var _binding : ViewBinding? = null
        get() = binding

    private val inflateMethod = classVB.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionCheck()
    }

    private fun PermissionCheck() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = inflateMethod.invoke(null, inflater, container, false) as VB
        viewModel = createViewModelLazy(classVM.kotlin, { viewModelStore }).value
        window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
        enterTransition = MaterialFadeThrough()
        returnTransition = MaterialFadeThrough()


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}