package example.testproject.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import example.testproject.R
import example.testproject.adapters.ViewPagerAdapter
import example.testproject.databinding.FragmentContainerBinding

private lateinit var binding: FragmentContainerBinding

class ContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_container, container, false
        )
        binding.lifecycleOwner = this

        binding.viewPager.adapter =
            ViewPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        return binding.root
    }


}
