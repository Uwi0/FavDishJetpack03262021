package com.kakapo.favoritdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kakapo.favoritdish.R
import com.kakapo.favoritdish.application.FavDishApplication
import com.kakapo.favoritdish.databinding.ActivityAddUpdateDishBinding
import com.kakapo.favoritdish.databinding.DialogCustomImageSelectionBinding
import com.kakapo.favoritdish.databinding.DialogCustomListBinding
import com.kakapo.favoritdish.model.entities.FavDish
import com.kakapo.favoritdish.utils.Constants
import com.kakapo.favoritdish.view.adapter.CustomListItemAdapter
import com.kakapo.favoritdish.viewmodel.FavDishViewModel
import com.kakapo.favoritdish.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "FavDishDirectory"
    }

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""
    private lateinit var mCustomListDialog: Dialog
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupActionBar()
        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
       v?.let {
           when(v.id){
               R.id.iv_add_dish_image ->{
                   customImageSelectionDialog()
                   return
               }
               R.id.et_type ->{
                   val typeTitle = resources.getString(R.string.title_select_dish_type)
                   customItemListDialog(
                       typeTitle,
                       Constants.dishTypes(),
                       Constants.DISH_TYPE
                   )
                   return
               }
               R.id.et_category ->{
                   val categoryTitle = resources.getString(R.string.title_select_dish_category)
                   customItemListDialog(
                       categoryTitle,
                       Constants.dishCategory(),
                       Constants.DISH_CATEGORY
                   )
                   return
               }
               R.id.et_cooking_time ->{
                   val cookingTimeTitle = resources.getString(R.string.title_select_dish_cooking_time)
                   customItemListDialog(
                       cookingTimeTitle,
                       Constants.dishCookingTime(),
                       Constants.DISH_COOKING_TIME
                   )
               }
               R.id.btn_add_dish ->{
                   val title = mBinding.etTitle.text.toString().trim{ it <= ' ' }
                   val type = mBinding.etType.text.toString().trim{ it <= ' ' }
                   val category = mBinding.etCategory.text.toString().trim{ it <= ' ' }
                   val ingredients = mBinding.etIngredients.text.toString().trim{ it <= ' ' }
                   val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim{ it <= ' ' }
                   val cookingDirection = mBinding.etDirectionToCook.text.toString().trim{ it <= ' '}

                   when{
                       TextUtils.isEmpty(mImagePath) -> {
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_select_dish_image),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       TextUtils.isEmpty(title) ->{
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_enter_dish_title),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       TextUtils.isEmpty(type) ->{
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_select_dish_type),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       TextUtils.isEmpty(category) ->{
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_select_dish_category),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       TextUtils.isEmpty(ingredients) ->{
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_enter_dish_ingredients),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       TextUtils.isEmpty(cookingTimeInMinutes) ->{
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_select_dish_cooking_time),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       TextUtils.isEmpty(cookingDirection) ->{
                           Toast.makeText(
                               this,
                               resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       else ->{
                           val favDishDetails: FavDish = FavDish(
                               mImagePath,
                               Constants.DISH_IMAGE_SOURCE_LOCAL,
                               title,
                               type,
                               category,
                               ingredients,
                               cookingTimeInMinutes,
                               cookingDirection,
                               false
                           )

                           mFavDishViewModel.insertDish(favDishDetails)
                           Toast.makeText(
                               this,
                               "You Successfully added your favorite dish details",
                               Toast.LENGTH_SHORT
                           ).show()
                           Log.i("Insertion", "Success")
                           finish()
                       }
                   }
               }
               else ->{

               }
           }
       }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA) {
                data?.let {
                    val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
//                    mBinding.ivDishImage.setImageBitmap(thumbnail)
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    mImagePath = saveImageToInternalStorage(bitmap = thumbnail)
                    Log.i("ImagePath", mImagePath)

                    mBinding.ivAddDishImage
                        .setImageDrawable(
                            ContextCompat.getDrawable(this, R.drawable.ic_vector_edit_24)
                        )
                }
            }
            if(requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoURI = data.data
                    Glide.with(this)
                        .load(selectedPhotoURI)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                    Log.i("ImagePath", mImagePath)
                                }

                                return false
                            }

                        })
                        .into(mBinding.ivDishImage)

                    mBinding.ivAddDishImage
                        .setImageDrawable(
                            ContextCompat.getDrawable(this, R.drawable.ic_vector_edit_24)
                        )
                }
            }
        }else if(resultCode == Activity.RESULT_CANCELED){
            Log.e("canceled", "pick image from media Canceled onActivityResult")
        }
    }

    private fun customImageSelectionDialog(){
        val dialog = Dialog(this)
        val binding : DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener{
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                           val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA)
                        }else{
                            showRationalDialogForPermission()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                   showRationalDialogForPermission()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener{
            Dexter.withContext(this@AddUpdateDishActivity).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@AddUpdateDishActivity,
                        "You have denied the storage permission to select image",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }


            }).onSameThread().check()

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupActionBar(){
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun showRationalDialogForPermission(){
        AlertDialog.Builder(this).setMessage("It Looks like you have turned off permissions" +
                " required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS"){ _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    Log.e("Error permission", "error give permissions AddUpdateActivity " +
                            "shownRationalDialogPermission")
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String{
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            Log.e("Error", "Error while try to compress image to storage saveImageToInternal Storage")
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemListDialog(title: String, list: List<String>, selection: String){
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this, list, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constants.DISH_TYPE ->{
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY ->{
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME ->{
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }
}