package com.kakapo.favoritdish.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kakapo.favoritdish.R
import com.kakapo.favoritdish.application.FavDishApplication
import com.kakapo.favoritdish.databinding.FragmentDishDetailBinding
import com.kakapo.favoritdish.viewmodel.FavDishViewModel
import com.kakapo.favoritdish.viewmodel.FavDishViewModelFactory
import java.io.IOException
import java.util.*

class DishDetailFragment : Fragment() {

    private var mBinding: FragmentDishDetailBinding? = null
    private val mFavDishViewModel: FavDishViewModel by viewModels{
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        mBinding = FragmentDishDetailBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailFragmentArgs by navArgs()

        args.let{
            try{
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object: RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("TAG", "Error loading Image", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let{
                                Palette.from(resource.toBitmap()).generate{ palette ->
                                    val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                    mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)
                                }
                            }

                            return false
                        }

                    })
                    .into(mBinding!!.ivDishImage)
            }catch (e: IOException){
                Log.e("error load image", "error binding image from database", e)
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text = it.dishDetails.title
            mBinding!!.tvType.text = it.dishDetails.type.capitalize(Locale.ROOT)
            mBinding!!.tvCategory.text = it.dishDetails.category
            mBinding!!.tvIngredients.text = it.dishDetails.ingredients
            mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook
            mBinding!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)

            if (args.dishDetails.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )
            }

            mBinding!!.ivFavoriteDish.setOnClickListener {
                args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
                mFavDishViewModel.update(args.dishDetails)

                if (args.dishDetails.favoriteDish) {
                    mBinding!!.ivFavoriteDish.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_favorite_selected
                        )
                    )
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.msg_added_to_favortes),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    mBinding!!.ivFavoriteDish.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireActivity(),
                            R.drawable.ic_favorite_unselected
                        )
                    )
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.msg_removed_from_favortes),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

}