package com.photocard.secretdiary.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.photocard.secretdiary.R
import com.photocard.secretdiary.data.WriteInfo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_viewer.*



class ViewerActivity : AppCompatActivity() {
    private lateinit var mWriteInfo: WriteInfo
    private val mRealm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewer)

        mWriteInfo = intent.getParcelableExtra("data")
        setView()
    }

    private fun setView(){
        tv_view_date.text = mWriteInfo.date

        tv_viewer_tag.text = "#${mWriteInfo.tag}"
        tv_viewer_title.text = mWriteInfo.title

        when(mWriteInfo.weather){
            0 -> iv_weather.setImageResource(R.drawable.sun)
            1 -> iv_weather.setImageResource(R.drawable.cloudy)
            2 -> iv_weather.setImageResource(R.drawable.cloud)
            3 -> iv_weather.setImageResource(R.drawable.rainy)

        }

        re_viewer.setPadding(10, 20, 10, 20)
        re_viewer.setEditorFontSize(14)
        re_viewer.loadCSS("file:///android_asset/img.css")

        re_viewer.html = mWriteInfo.content

        tv_viewer_modify.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("data", mWriteInfo)
            startActivityForResult(intent, 300)
        }

        tv_viewer_delete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("편지를 삭제하시겠습니까?")
            builder.setPositiveButton("예"
            ) { dialog, _ ->
                mRealm.executeTransaction {
                    val writeInfo = mRealm.where(WriteInfo::class.java).equalTo("idx", mWriteInfo.idx).findFirst()
                    writeInfo?.deleteFromRealm()
                    Toast.makeText(this, "편지가 삭제되었습니다.", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    finish()
                }
            }
            builder.setNegativeButton("아니오"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                300 -> {
                    mWriteInfo = data?.getParcelableExtra("data") ?: mWriteInfo
                    setView()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
}
