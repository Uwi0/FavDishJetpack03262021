package com.kakapo.favoritdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kakapo.favoritdish.databinding.FragmentRandomDishesBinding
import com.kakapo.favoritdish.viewmodel.NotificationsViewModel

class RandomDishFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var mBinding: FragmentRandomDishesBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRandomDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}