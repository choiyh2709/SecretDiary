package com.photocard.secretdiary.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.BaseActivity
import com.photocard.secretdiary.data.UserInfo
import com.photocard.secretdiary.data.WriteInfo
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import gun0912.tedbottompicker.TedBottomPicker
import io.realm.Realm
import io.realm.Sort
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.editor_custom_tool.*
import java.text.SimpleDateFormat
import java.util.*


class EditActivity : BaseActivity() {
    private var isBold = false
    private var isItalic = false
    private var isHead = false
    private var isLeft = false
    private var isCenter = false
    private var isRight = false
    private var isTemporaryStorage = true
    private val mRealm = Realm.getDefaultInstance()
    private val mContext: Context by lazy {
        this@EditActivity
    }
    private var mWeather = 0
    private var isModify = false
    private var mWriteInfo: WriteInfo? = null
    private val RC_GALLERY = 100
    private lateinit var mInterstitialAd: InterstitialAd

    private var mDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-8610461591818507/7534871365"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdClosed() {
                setEdit()
            }
        }

        setTitleView()
        setEditorView()
        setWeatherView()
        setModifySetting()
        setOnClickListener()
    }

    private fun setTitleView() {
        et_editor_tag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isTemporaryStorage = false
                val cnt = s.length
                tv_editor_tag_cnt.text = "$cnt/12"
            }
        })

        et_editor_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isTemporaryStorage = false
                val cnt = s.length
                tv_editor_title_cnt.text = "$cnt/20"
            }
        })


        val mSimpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        mDate = mSimpleDateFormat.format(Date())
        tv_edit_date.text = mDate

        val calendar = Calendar.getInstance()
        var mYear = calendar.get(Calendar.YEAR)
        var mMonth = calendar.get(Calendar.MONTH)
        var mDay = calendar.get(Calendar.DAY_OF_MONTH)

        rl_editor_date.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val mPhotoDate = if (monthOfYear < 9) {
                        if (dayOfMonth < 10) {
                            """$year.0${monthOfYear + 1}.0$dayOfMonth"""
                        } else {
                            """$year.0${monthOfYear + 1}.$dayOfMonth"""
                        }
                    } else {
                        if (dayOfMonth < 10) {
                            """$year.${monthOfYear + 1}.0$dayOfMonth"""
                        } else {
                            """$year.${monthOfYear + 1}.$dayOfMonth"""
                        }
                    }
                    mYear = year
                    mMonth = monthOfYear
                    mDay = dayOfMonth
                    tv_edit_date.text = mPhotoDate

                }, mYear, mMonth, mDay)

            datePickerDialog.show()
        }

        rv_weather.setOnClickListener {
            if (ll_weather_list.visibility == View.GONE) {
                ll_weather_list.visibility = View.VISIBLE
            } else {
                ll_weather_list.visibility = View.GONE
            }
        }
    }

    private fun setEditorView() {
        re_editor.setPadding(10, 20, 10, 20)
        re_editor.setEditorFontSize(14)
        re_editor.setPlaceholder("내용")
        re_editor.loadCSS("file:///android_asset/img.css")
        re_editor.setOnDecorationChangeListener { _, list -> changeStatus(list) }

        val userInfo = mRealm.where(UserInfo::class.java).findFirst()
        var style = ""
        when(userInfo?.font){
            0 -> {
                val type = ResourcesCompat.getFont(this, R.font.nanum_gothic)
                et_editor_tag.typeface = type
                et_editor_title.typeface = type
                style = "<style>\n" +
                        "@import url('https://fonts.googleapis.com/css?family=Nanum+Gothic+Coding&display=swap');\n" +
                        "*{font-family: 'Nanum Gothic Coding', monospace;}\n" +
                        "</style>"

            }
            1 -> {
                val type = ResourcesCompat.getFont(this, R.font.bmjua)
                et_editor_tag.typeface = type
                et_editor_title.typeface = type
                style = "<style>\n" +
                        "@import url('https://fonts.googleapis.com/css?family=Jua&display=swap');\n" +
                        "*{font-family: 'Jua';}\n" +
                        "</style>"
            }
            2 -> {
                val type = ResourcesCompat.getFont(this, R.font.gamja_flower)
                et_editor_tag.typeface = type
                et_editor_title.typeface = type
                style = "<style>\n" +
                        "@import url('https://fonts.googleapis.com/css?family=Gamja+Flower&display=swap');\n" +
                        "*{font-family: 'Gamja Flower', cursive;}\n"+
                        "</style>"
            }
        }

        re_editor.html = style

        re_editor.setOnTextChangeListener {
            isTemporaryStorage = false
        }
    }

    private fun setOnClickListener() {
        rl_editor_bold.setOnClickListener {
            re_editor.focusEditor()

            if (!isBold) {
                isBold = true
                rl_editor_bold.setBackgroundResource(R.color.back_line)
            } else {
                isBold = false
                rl_editor_bold.setBackgroundResource(R.color.white)
            }

            re_editor.setBold()
        }

        rl_editor_italic.setOnClickListener {
            re_editor.focusEditor()

            if (!isItalic) {
                isItalic = true
                rl_editor_italic.setBackgroundResource(R.color.back_line)
            } else {
                isItalic = false
                rl_editor_italic.setBackgroundResource(R.color.white)
            }

            re_editor.setItalic()
        }

        rl_editor_head.setOnClickListener {
            re_editor.focusEditor()

            if (!isHead) {
                isHead = true
                rl_editor_head.setBackgroundResource(R.color.back_line)
            } else {
                isHead = false
                rl_editor_head.setBackgroundResource(R.color.white)
            }

            re_editor.setHeading(1)
        }

        rl_editor_left.setOnClickListener {
            re_editor.focusEditor()

            if (!isLeft) {
                isLeft = true
                isCenter = false
                isRight = false
                rl_editor_right_back.setBackgroundResource(R.color.white)
                rl_editor_center_back.setBackgroundResource(R.color.white)
                rl_editor_left_back.setBackgroundResource(R.color.back_line)
            }

            re_editor.setAlignLeft()
        }

        rl_editor_center.setOnClickListener {
            re_editor.focusEditor()

            if (!isCenter) {
                isLeft = false
                isCenter = true
                isRight = false
                rl_editor_right_back.setBackgroundResource(R.color.white)
                rl_editor_center_back.setBackgroundResource(R.color.back_line)
                rl_editor_left_back.setBackgroundResource(R.color.white)
            }

            re_editor.setAlignCenter()
        }

        rl_editor_right.setOnClickListener {
            re_editor.focusEditor()

            if (!isRight) {
                isLeft = false
                isCenter = false
                isRight = true
                rl_editor_right_back.setBackgroundResource(R.color.back_line)
                rl_editor_center_back.setBackgroundResource(R.color.white)
                rl_editor_left_back.setBackgroundResource(R.color.white)
            }

            re_editor.setAlignRight()
        }

        rl_editor_image.setOnClickListener {
            re_editor.focusEditor()
//
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            intent.type = "image/*"
//            startActivityForResult(intent, RC_GALLERY)
            TedBottomPicker.with(this@EditActivity)
                .show {
                    val contentUri = it
                    val filePath = getRealPathFromURI(contentUri)
                    if (filePath != null && !filePath.contains("gif")) run {
                        CropImage.activity(contentUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this)
                    } else {
                        re_editor.insertImage(contentUri.toString(), "photo")
                    }
                }
        }

        tv_editor_edit.setOnClickListener {
            if (isEmptyTitleAndContents()) {
                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                } else {
                    setEdit()
                }
            }
        }
    }

    private fun isEmptyTitleAndContents(): Boolean {
        if (et_editor_tag.text.toString().trim { it <= ' ' }.isEmpty()) {
            showMessage("태그를 입력해 주세요")
            return false
        } else if (et_editor_title.text.toString().trim { it <= ' ' }.isEmpty()) {
            showMessage("제목을 입력해 주세요")
            return false
        } else if (re_editor.html == null || re_editor.html.trim { it <= ' ' }.isEmpty()) {
            showMessage("내용을 입력해 주세요")
            return false
        }
        return true
    }

    private fun setEdit() {
        mRealm.executeTransaction {
            if (isModify) {
                val result = mRealm.where(WriteInfo::class.java).equalTo("idx", mWriteInfo?.idx).findFirst()
                result?.let {
                    it.tag = et_editor_tag.text.toString().replace("#", "")
                    it.title = et_editor_title.text.toString()
                    it.date = tv_edit_date.text.toString()
                    it.weather = mWeather
                    it.content = re_editor.html

                    setResult(Activity.RESULT_OK, intent.putExtra("data", it))
                    showMessage("편지가 수정되었습니다.")
                }
            } else {
                val result = mRealm.where(WriteInfo::class.java).sort("idx", Sort.DESCENDING).findAll()

                val size = result.size
                val idx = if (size > 0) result.first()?.idx?.plus(1) else 1

                val writeInfo = mRealm.createObject(WriteInfo::class.java, idx)
                writeInfo.tag = et_editor_tag.text.toString().replace("#", "")
                writeInfo.title = et_editor_title.text.toString()
                writeInfo.date = tv_edit_date.text.toString()
                writeInfo.weather = mWeather
                writeInfo.content = re_editor.html

                if (mRealm.where(WriteInfo::class.java).equalTo("insDate", mDate).findAll().size == 0){
                    val userInfo = mRealm.where(UserInfo::class.java).findFirst() ?: return@executeTransaction
                    userInfo.point = userInfo.point.plus(1)
                }
                writeInfo.insDate = mDate

                setResult(Activity.RESULT_OK)
                showMessage("편지가 등록되었습니다.")
            }
            finish()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun changeStatus(list: List<RichEditor.Type>) {
        val status = ArrayList<String>()
        for (i in list.indices) {
            status.add(list[i].name)
        }

        if (status.contains("BOLD")) {
            isBold = true
            rl_editor_bold.setBackgroundResource(R.color.back_line)
        } else {
            isBold = false
            rl_editor_bold.setBackgroundResource(R.color.white)
        }

        if (status.contains("ITALIC")) {
            isItalic = true
            rl_editor_italic.setBackgroundResource(R.color.back_line)
        } else {
            isItalic = false
            rl_editor_italic.setBackgroundResource(R.color.white)
        }

        if (status.contains("H1")) {
            isHead = true
            rl_editor_heading_back.setBackgroundResource(R.color.back_line)
        } else {
            isHead = false
            rl_editor_heading_back.setBackgroundResource(R.color.white)
        }

        when {
            status.contains("JUSTIFYRIGHT") -> {
                isRight = true
                isCenter = false
                isLeft = false
                re_editor.setAlignRight()
                rl_editor_left_back.setBackgroundResource(R.color.white)
                rl_editor_center_back.setBackgroundResource(R.color.white)
                rl_editor_right_back.setBackgroundResource(R.color.back_line)
            }
            status.contains("JUSTIFYCENTER") -> {
                isRight = false
                isCenter = true
                isLeft = false
                rl_editor_left_back.setBackgroundResource(R.color.white)
                rl_editor_center_back.setBackgroundResource(R.color.back_line)
                rl_editor_right_back.setBackgroundResource(R.color.white)
            }
            else -> {
                isRight = false
                isCenter = false
                isLeft = true
                rl_editor_left_back.setBackgroundResource(R.color.back_line)
                rl_editor_center_back.setBackgroundResource(R.color.white)
                rl_editor_right_back.setBackgroundResource(R.color.white)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    if (data == null) return
                    val result = CropImage.getActivityResult(data)
                    re_editor.insertImage(result.uri.toString(), "photo")
                }
            }
        }
    }

    private fun setModifySetting() {
        mWriteInfo = intent.getParcelableExtra("data")

        mWriteInfo?.let {
            isModify = true

            tv_edit_date.text = it.date

            mWeather = it.weather?:0

            when (it.weather) {
                0 -> iv_weather.setImageResource(R.drawable.sun)
                1 -> iv_weather.setImageResource(R.drawable.cloudy)
                2 -> iv_weather.setImageResource(R.drawable.cloud)
                3 -> iv_weather.setImageResource(R.drawable.rainy)

            }

            et_editor_tag.setText(it.tag)
            et_editor_title.setText(it.title)
            re_editor.html = it.content
        }
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        try {
            val filePath: String?
            val cursor = contentResolver.query(contentURI, null, null, null, null)
            if (cursor == null) {
                filePath = contentURI.path
            } else {
                cursor.moveToFirst()
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                filePath = cursor.getString(idx)
                cursor.close()
            }
            return filePath
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "파일이 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            return ""
        }
    }

    private fun setWeatherView() {
        rv_sun.setOnClickListener {
            iv_weather.setImageResource(R.drawable.sun)
            mWeather = 0
            ll_weather_list.visibility = View.GONE
        }

        rv_cloudy.setOnClickListener {
            iv_weather.setImageResource(R.drawable.cloudy)
            mWeather = 1
            ll_weather_list.visibility = View.GONE
        }

        rv_cloud.setOnClickListener {
            iv_weather.setImageResource(R.drawable.cloud)
            mWeather = 2
            ll_weather_list.visibility = View.GONE
        }

        rv_rainy.setOnClickListener {
            iv_weather.setImageResource(R.drawable.rainy)
            mWeather = 3
            ll_weather_list.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
}
