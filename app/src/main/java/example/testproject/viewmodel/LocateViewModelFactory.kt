package example.testproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context

class LocateViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocateViewModel::class.java)) {
            return LocateViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}