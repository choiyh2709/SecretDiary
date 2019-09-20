package com.photocard.secretdiary.view


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.photocard.secretdiary.R
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_save_user.*


class SaveUserFragment : Fragment() {
    private var mProfileUri: String? = null
    private val cGallery = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_user, container, false)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == cGallery) {
                mProfileUri = data?.data.toString()
                Glide.with(this).load(data?.data).centerCrop().bitmapTransform(CropCircleTransformation(context)).into(iv_user_profile)
            }
        }
    }

    fun getUserName(): String{
        return  et_user_name.text.toString()
    }

    fun getUserProfile(): String?{
        return mProfileUri
    }
}
