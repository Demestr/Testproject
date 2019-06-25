package example.testproject

import android.Manifest
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import example.testproject.databinding.ActivityMainBinding
import example.testproject.viewmodel.LocateViewModel
import example.testproject.viewmodel.LocateViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: LocateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProviders.of(this,
            LocateViewModelFactory(this)
        ).get(LocateViewModel::class.java)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            Snackbar.make(binding.root, "Нет разрешения для использования GPS!", Snackbar.LENGTH_LONG).show()
        } else {
            viewModel.getCurrentLocate()
        }
    }
}
