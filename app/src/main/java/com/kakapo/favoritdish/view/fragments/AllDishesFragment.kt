package com.kakapo.favoritdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.kakapo.favoritdish.R
import com.kakapo.favoritdish.application.FavDishApplication
import com.kakapo.favoritdish.databinding.FragmentAllDishesBinding
import com.kakapo.favoritdish.view.activities.AddUpdateDishActivity
import com.kakapo.favoritdish.view.adapter.FavDishAdapter
import com.kakapo.favoritdish.viewmodel.FavDishViewModel
import com.kakapo.favoritdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding
    private var _binding: FragmentAllDishesBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val favDishAdapter = FavDishAdapter(this)

        mBinding.rvDishesList.adapter = favDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){ dishes ->
            dishes?.let{
                if (it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE

                    favDishAdapter.dishesList(it)
                }else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dish -> {
                val intent = Intent(requireActivity(), AddUpdateDishActivity ::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}