package example.testproject.fragments

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import example.testproject.viewmodel.LocateViewModel
import example.testproject.R
import example.testproject.adapters.TableAdapter
import example.testproject.databinding.FragmentTableBinding


class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding
    private lateinit var viewModel: LocateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_table, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        activity.let {
            viewModel = ViewModelProviders.of(activity!!).get(LocateViewModel::class.java)
        }

        viewModel.listLocates.observe(this, Observer { listLocateData ->
            if (listLocateData.isEmpty())
                binding.fabDelete.hide()
            else
                binding.fabDelete.show()
            val adapter = TableAdapter(
                context!!,
                TableAdapter.OnClickListener { clickedLocateData ->
                    findNavController().navigate(
                        ContainerFragmentDirections.actionContainerFragmentToDetailFragment(clickedLocateData)
                    )
                })
            adapter.setItems(listLocateData)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
        })

        binding.fabDelete.setOnClickListener {
            viewModel.clearDatabase()
        }
        return binding.root
    }
}
