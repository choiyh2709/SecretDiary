package com.photocard.secretdiary.view


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.photocard.secretdiary.R
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_save_target.*
import java.text.SimpleDateFormat
import java.util.*


class SaveTargetFragment : Fragment() {
    private var mProfileUri: String? = null
    private lateinit var mContext: Context
    private val cGallery = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_target, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
    }

    private fun setView(){
        iv_user_profile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, cGallery)
        }

        tv_user_day.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(mContext, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val mPhotoDate = if (monthOfYear < 9){
                    if(dayOfMonth < 10) {
                        """$year.0${monthOfYear + 1}.0$dayOfMonth"""
                    }else{
                        """$year.0${monthOfYear + 1}.$dayOfMonth"""
                    }
                }else{
                    if(dayOfMonth < 10){
                        """$year.${monthOfYear + 1}.0$dayOfMonth"""
                    }else{
                        """$year.${monthOfYear + 1}.$dayOfMonth"""
                    }
                }
                tv_user_day.text = mPhotoDate

            }, year, month, day)
            datePickerDialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == cGallery) {
                mProfileUri = data?.data.toString()
                Glide.with(mContext).load(data?.data).centerCrop().apply(
                    RequestOptions.bitmapTransform(
                        CropCircleTransformation()
                    )).into(iv_user_profile)
            }
        }
    }

    fun getTargetName(): String{
        return  et_user_name.text.toString()
    }

    fun getTargetProfile(): String?{
        return mProfileUri
    }

    fun getDay(): String?{
        return if (tv_user_day.text.toString().trim().isNotEmpty()) {
            tv_user_day.text.toString().replace(".", "")
        }else{
            val mSimpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
            mSimpleDateFormat.format(Date())
        }
    }
}
