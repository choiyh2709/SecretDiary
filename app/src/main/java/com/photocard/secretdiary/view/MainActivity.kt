package com.photocard.secretdiary.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photocard.secretdiary.R
import com.photocard.secretdiary.data.UserInfo
import com.photocard.secretdiary.data.WriteInfo
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), TimelineHeaderViewHolder.OnTimelineHeaderListener {
    private val mContext: Context by lazy { this@MainActivity }
    private val cEdit = 200
    private val cSettings = 300
    private val cGalleryUser = 100
    private val cGalleryTarget = 101
    private lateinit var mAdapter: TimelineAdapter
    private lateinit var mUserInfo: UserInfo
    private var isFabOpen = false
    private val mRealm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    private fun setView(){
        rv_main_timeline.layoutManager = LinearLayoutManager(this)
        mAdapter = TimelineAdapter(getLocalData(), this)
        rv_main_timeline.adapter = mAdapter

        fab_main.setOnClickListener {
            if (isFabOpen){
                isFabOpen = false
                visibleFab(isFabOpen)
            }else{
                isFabOpen = true
                visibleFab(isFabOpen)
            }
        }

        fab_write.setOnClickListener {
            if (it.visibility == View.VISIBLE)
            startActivityForResult(Intent(this, EditActivity::class.java), cEdit)
        }

        fab_settings.setOnClickListener {
            if (it.visibility == View.VISIBLE)
            startActivityForResult(Intent(this, SettingActivity::class.java), cSettings)
        }

        rv_main_timeline.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isFabOpen){
                    isFabOpen = false
                    visibleFab(false)
                }

                if (dy > 10){
                    if (fab_main.isShown){
                        fab_main.hide()
                    }
                }else if (dy < -10){
                    if (!fab_main.isShown){
                        fab_main.show()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                cEdit -> {
                    //글쓰기
                    mAdapter.setData(getLocalData())
                    mAdapter.notifyDataSetChanged()
                }

                cGalleryUser -> {
                    //갤러리 (나)
                    mRealm.executeTransaction {
                        val userInfo = mRealm.where(UserInfo::class.java).findFirst()
                        userInfo?.userProfileImg = data?.data.toString()
                        mAdapter.notifyItemChanged(0)
                    }
                }

                cGalleryTarget -> {
                    //갤러리 (상대)
                    mRealm.executeTransaction {
                        val userInfo = mRealm.where(UserInfo::class.java).findFirst()
                        userInfo?.targetProfileImg = data?.data.toString()
                        mAdapter.notifyItemChanged(0)
                    }
                }

                cSettings -> {
                    //설정 화면
                }
            }
        }
    }

    private fun visibleFab(visible: Boolean){
        if (!visible){
            fab_main.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue))
            fab_main.setImageResource(R.drawable.plus)
            fab_write.hide()
            fab_settings.hide()
        }else{
            fab_main.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
            fab_main.setImageResource(R.drawable.cancel)
            fab_write.show()
            fab_settings.show()
        }
    }

    override fun onClickUserProfile() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, cGalleryUser)
    }

    override fun onClickTargetProfile() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, cGalleryTarget)
    }

    override fun onClickDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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

            mRealm.executeTransaction {
                val userInfo = mRealm.where(UserInfo::class.java).findFirst()
                userInfo?.day = mPhotoDate.replace(".", "")
                mAdapter.notifyItemChanged(0)
            }

        }, year, month, day)
        datePickerDialog.show()
    }

    private fun getLocalData(): RealmResults<WriteInfo> {
        mUserInfo = mRealm.where(UserInfo::class.java).findFirst()!!
        var localList = mRealm.where(WriteInfo::class.java).findAll()
        localList = localList.sort("idx", Sort.DESCENDING).sort("date", Sort.DESCENDING)
        if (localList.size == 0){
            val txt = tv_empty.text.toString()
            tv_empty.text = txt.replace("(target)", mUserInfo.targetName ?: "좋아하는")
            tv_empty.visibility = View.VISIBLE
            tv_empty.setOnClickListener {
                startActivityForResult(Intent(this, EditActivity::class.java), cEdit)
            }
        }else{
            tv_empty.visibility = View.GONE
        }
        mRealm.addChangeListener {
            mAdapter.notifyDataSetChanged()
        }
        return localList
    }
}
