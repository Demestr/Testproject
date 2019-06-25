package example.testproject.fragments


import android.Manifest
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import example.testproject.viewmodel.LocateViewModel
import example.testproject.viewmodel.OperationState
import example.testproject.R
import example.testproject.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val locatePermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: LocateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        activity.let {
            viewModel = ViewModelProviders.of(activity!!).get(LocateViewModel::class.java)
            binding.viewModel = viewModel
        }

        viewModel.state.observe(this, Observer {
            when (it) {
                OperationState.START -> {
                    getLocate()
                }
                OperationState.GET_LOCATION -> {
                    binding.messageTxt.text = "Получение координат"; binding.progressBar.visibility = View.VISIBLE
                }
                OperationState.GET_ADDRESS -> {
                    binding.messageTxt.text = "Получение адреса"; binding.progressBar.visibility = View.VISIBLE
                }
                OperationState.GET_WEATHER -> {
                    binding.messageTxt.text = "Получение погоды"; binding.progressBar.visibility = View.VISIBLE
                }
                OperationState.COMPLETE -> {
                    binding.messageTxt.text = ""; binding.progressBar.visibility = View.GONE
                }
                OperationState.ERROR -> {
                    binding.messageTxt.text = "Ошибка!"; binding.progressBar.visibility = View.GONE
                }
            }
        })

        return binding.root
    }

    private fun getLocate() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity!!, locatePermission, 0)
        } else {
            viewModel.getCurrentLocate()
        }
    }
}
