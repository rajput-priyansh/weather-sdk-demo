package com.vibs.weatherdemosdk.base

import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.vibs.weatherdemosdk.dialog.ProgressDialog


open class BaseFragment(private val resLayout: Int) : Fragment(resLayout) {
    private var progressDialog: ProgressDialog? = null
    private var permissionCallback: PermissionCallback? = null
    private var startActivityCallback: StartActivityCallback? = null

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

    open fun initData() {

    }

    open fun initUi() {
    }

    open fun observer() {

    }

    open fun refreshOnInternet() {

    }

    /**
     * Check whether a permission is granted or not.
     *
     * @param permission
     * @return
     */
    open fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            permission
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
            progressDialog = ProgressDialog(requireContext(), isDismissOnBack)
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

    fun showNoInternetDialog() {
        (activity as BaseActivity).showNoInternetDialog()
    }

    fun hideNoInternetDialog() {
        (activity as BaseActivity).hideNoInternetDialog()
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
     * Use to occur on success dialog close
     */
    open fun onSuccessDialogClose(dialogTag: Int) {

    }

    fun showToast(msg: String?) {
        if (msg.isNullOrBlank()) return
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}