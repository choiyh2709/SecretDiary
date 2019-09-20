package com.photocard.secretdiary.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.photocard.secretdiary.R
import com.photocard.secretdiary.custom.RealmBackupRestore
import com.photocard.secretdiary.data.UserInfo
import com.photocard.secretdiary.data.WriteInfo
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_setting.*



class SettingActivity : AppCompatActivity() {
    private val cLockSetting = 100
    private val cPasswordSetting = 101
    private var mUserInfo: UserInfo? = null
    private val mRealm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    private fun setView(){
        mUserInfo = mRealm.where(UserInfo::class.java).findFirst()

        tv_letter_cnt.text = "x${mUserInfo?.point}"

        mUserInfo?.let {
            swh_lock.isChecked = it.isLock
        }

        swh_lock.setOnCheckedChangeListener { _, b ->
            if (b){
                if (mUserInfo?.password != null) {
                    //비밀번호 설정 o
                    mRealm.executeTransaction {
                        mUserInfo?.isLock = true
                        Toast.makeText(this, "잠금화면이 설정되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    //비밀번호 설정 x
                    val intent = Intent(this@SettingActivity, LockActivity::class.java)
                    intent.putExtra("action", 1)
                    startActivityForResult(intent, cPasswordSetting)
                }
            }else{
                mRealm.executeTransaction {
                    mUserInfo?.isLock = false
                    Toast.makeText(this, "잠금화면이 해제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ll_data_clear.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("정말 편지를 모두 지우시겠습니까?")
            builder.setPositiveButton("예"
            ) { dialog, _ ->
                mRealm.executeTransaction {
                    val writeInfo = mRealm.where(WriteInfo::class.java).findAll()
                    writeInfo?.deleteAllFromRealm()
                    Toast.makeText(this, "편지가 모두 삭제되었습니다.", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            builder.setNegativeButton("아니오"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        ll_password_change.setOnClickListener {
            val intent = Intent(this@SettingActivity, LockActivity::class.java)
            intent.putExtra("action", 2)
            startActivity(intent)
        }

        ll_data_backup.setOnClickListener {
            RealmBackupRestore(this).backup()
        }

        ll_data_restore.setOnClickListener {
            val permissionListener = object : PermissionListener {
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                }

                override fun onPermissionGranted() {
                    RealmBackupRestore(this@SettingActivity).restore()
                }
            }

            TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("데이터 복구를 하기 위해 저장공간 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        }

        ll_app_share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=${this.packageName}")
            startActivity(intent)
        }

        ll_app_review.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}"))
            startActivity(intent)
        }

        ll_app_email.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            try {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))

                emailIntent.type = "text/html"
                emailIntent.setPackage("com.google.android.gm")
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("cyh2709@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "문의/개선/제안 사항")
                if (emailIntent.resolveActivity(packageManager) != null)
                    startActivity(emailIntent)

                startActivity(emailIntent)
            } catch (e: Exception) {
                e.printStackTrace()

                emailIntent.type = "text/html"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("cyh2709@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "문의/개선/제안 사항")

                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            }

        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            cLockSetting -> {
                if( resultCode == Activity.RESULT_OK ){
                    mRealm.executeTransaction {
                        mUserInfo?.isLock = true
                    }
                }else{
                    swh_lock.isChecked = false
                }
            }
        }
    }
}
