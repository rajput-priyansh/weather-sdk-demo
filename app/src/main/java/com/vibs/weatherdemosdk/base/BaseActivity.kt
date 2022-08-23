package com.vibs.weatherdemosdk.base

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vibs.weatherdemosdk.BuildConfig
import com.vibs.weatherdemosdk.dialog.NoInternetDialog
import com.vibs.weatherdemosdk.dialog.ProgressDialog


open class BaseActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    private var progressDialog: ProgressDialog? = null
    private var permissionCallback: PermissionCallback? = null
    private var startActivityCallback: StartActivityCallback? = null
    var noInternetDialog: NoInternetDialog? = null



    private var activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            startActivityCallback?.onActivityResult(result)
        }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val grantedPermissions = arrayListOf<String>()
            val deniedPermissions = arrayListOf<String>()

            permissions.entries.forEach { permission ->
                if (permission.value) {
                    grantedPermissions.add(permission.key)
                } else {
                    deniedPermissions.add(permission.key)
                }
            }
            permissionCallback?.onPermissionGranted(grantedPermissions)
            permissionCallback?.onPermissionDenied(deniedPermissions)
        }

    override fun onDestroy() {
        hideProgressDialog()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    open fun initData() {
    }

    open fun initUi() {
    }

    open fun observer() {

    }

    /**
     * Check whether a permission is granted or not.
     *
     * @param permission
     * @return
     */
    open fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this@BaseActivity, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request permissions and get the result on callback.
     *
     * @param permissions - list of permissions
     * @param callback - callback instance
     */
    open fun requestPermission(permissions: Array<String>, callback: PermissionCallback?) {
        this.permissionCallback = callback
        requestPermissionLauncher.launch(permissions)
    }

    /**
     * Request permission and get the result on callback.
     *
     * @param permission - single permission
     * @param callback - callback instance
     */
    open fun requestPermission(permission: String, callback: PermissionCallback?) {
        this.permissionCallback = callback
        requestPermissionLauncher.launch(arrayOf(permission))
    }

    /**
     * @param isDismissOnBack - if false then dialog will not dismiss on destroy
     * Use to display progress dialog
     */
    open fun showProgressDialog(isDismissOnBack: Boolean = true) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this@BaseActivity, isDismissOnBack)
        }
        progressDialog?.show()
    }

    /**
     * Use to hide progress dialog
     */
    open fun hideProgressDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }

    /**
     * Use to manage Apis error
     */
    open fun manageOnApiError(message: String?) {
    }

    /**
     * Use to manage Apis success
     */
    open fun manageOnApiSuccess(message: String) {
    }

    open fun startNewActivityForResult(intent: Intent, callback: StartActivityCallback) {
        activityLauncher.launch(intent)
        this.startActivityCallback = callback
    }

    /**
     * Use to manage activity toolbar
     */
    open fun AppCompatActivity.setAsActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * use to add or replace fragment in activity
     */
    open fun addFragmentReplace(container: Int?, fragment: Fragment?, addToBackStack: Boolean) {
        if (supportFragmentManager.isDestroyed) return
        if (container == null || fragment == null) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.name)
        }
        fragmentTransaction.replace(container, fragment, fragment.javaClass.name).commit()
    }


    fun showNoInternetDialog() {
        if (BuildConfig.DEBUG) Log.d(TAG, "showNoInternetDialog() called")
        if (noInternetDialog == null) {
            noInternetDialog = NoInternetDialog(this) {
                refreshInternetView()
            }
        }
        noInternetDialog?.show()
    }

    fun hideNoInternetDialog() {
        if (BuildConfig.DEBUG) Log.d(TAG, "hideNoInternetDialog() called")
        noInternetDialog?.dismiss()
        noInternetDialog = null
    }


    private fun refreshInternetView() {
        if (BuildConfig.DEBUG) Log.d(TAG, "refreshInternetView() called")
        hideNoInternetDialog()
        refreshOnInternet()
    }

    open fun refreshOnInternet(){}
}