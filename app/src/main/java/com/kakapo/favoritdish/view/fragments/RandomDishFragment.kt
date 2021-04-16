package com.kakapo.favoritdish.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kakapo.favoritdish.databinding.FragmentRandomDishesBinding
import com.kakapo.favoritdish.viewmodel.NotificationsViewModel
import com.kakapo.favoritdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var mBinding: FragmentRandomDishesBinding? = null
    private lateinit var mRandomDishViewModel: RandomDishViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentRandomDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)

        mRandomDishViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner, { randomDishResponse ->
            randomDishResponse?.let {
                Log.i("Random Dish Response", "${randomDishResponse.recipes[0]}")
            }
        })

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, {dataError ->
            dataError?.let{
                Log.i("Random Dish Api Error", "$dataError")
            }
        })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner,{loadRandomDish ->
            loadRandomDish?.let{
                Log.i("Random Dish Loading", "$loadRandomDish")
            }
        })
    }
}