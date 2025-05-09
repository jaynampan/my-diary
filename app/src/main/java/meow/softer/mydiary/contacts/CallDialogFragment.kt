package meow.softer.mydiary.contacts

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ThemeManager.Companion.instance
import meow.softer.mydiary.shared.gui.MyDiaryButton
import java.util.Objects

class CallDialogFragment : DialogFragment(), View.OnClickListener {
    /**
     * UI
     */
    private var RL_contacts_call_name: RelativeLayout? = null
    private var TV_contacts_call_name: TextView? = null
    private var But_contacts_call_cancel: MyDiaryButton? = null
    private var But_contacts_call_call: MyDiaryButton? = null

    /**
     * Contacts Info
     */
    private var contactsName: String? = null
    private var contactsPhoneNumber: String? = null

    private var havePermission = false
    private var RequestPermissionsResult = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Objects.requireNonNull<Dialog?>(this.dialog).setCanceledOnTouchOutside(true)
        //触摸框外可以取消对话框
        val rootView = inflater.inflate(R.layout.dialog_fragment_contacts_call, container)

        RL_contacts_call_name = rootView.findViewById<RelativeLayout>(R.id.RL_contacts_call_name)
        TV_contacts_call_name = rootView.findViewById<TextView>(R.id.TV_contacts_call_name)

        RL_contacts_call_name!!.setBackgroundColor(instance!!.getThemeMainColor(requireActivity()))

        But_contacts_call_call = rootView.findViewById<MyDiaryButton>(R.id.But_contacts_call_call)
        But_contacts_call_call!!.setOnClickListener(this)
        But_contacts_call_cancel =
            rootView.findViewById<MyDiaryButton>(R.id.But_contacts_call_cancel)
        But_contacts_call_cancel!!.setOnClickListener(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkPermission(REQUEST_CALL_PHONE_PERMISSION)) {
            RequestPermissionsResult = true
            havePermission = true
        }
    }


    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission(), object : ActivityResultCallback<Boolean> {
            override fun onActivityResult(isGranted: Boolean) {
                if (isGranted) {
                    havePermission = true
                } else {
                    val builder = AlertDialog.Builder(activity)
                        .setTitle(getString(R.string.contacts_call_phone_permission_title))
                        .setMessage(getString(R.string.contacts_call_phone_permission_content))
                        .setPositiveButton(getString(R.string.dialog_button_ok), null)
                    builder.show()
                    havePermission = false
                }
            }
        }
    )


    override fun onResume() {
        super.onResume()
        if (RequestPermissionsResult) {
            if (havePermission) {
                contactsName = requireArguments().getString("contactsName", "")
                contactsPhoneNumber = requireArguments().getString("contactsPhoneNumber", "")
                if ("" != contactsName) {
                    TV_contacts_call_name!!.text = contactsName
                }
            } else {
                dismiss()
            }
        }
    }

    private fun checkPermission(requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                )
            ) {
                val permission = Manifest.permission.CALL_PHONE
                requestPermissionLauncher.launch(permission)
                return false
            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(), arrayOf<String>(Manifest.permission.CALL_PHONE),
                    requestCode
                )
                return false
            }
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_contacts_call_call -> {
                val tm =
                    requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                    //No module for calling phone
                    Toast.makeText(
                        activity,
                        getString(R.string.contacts_call_phone_no_call_function),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    //Can call phone
                    val intent = Intent(Intent.ACTION_CALL, "tel:$contactsPhoneNumber".toUri())
                    startActivity(intent)
                }
                dismiss()
            }

            R.id.But_contacts_call_cancel -> dismiss()
        }
    }

    companion object {
        /**
         * Permission
         */
        private const val REQUEST_CALL_PHONE_PERMISSION = 2
        fun newInstance(contactsName: String?, contactsPhoneNumber: String?): CallDialogFragment {
            val args = Bundle()
            val fragment = CallDialogFragment()
            args.putString("contactsName", contactsName)
            args.putString("contactsPhoneNumber", contactsPhoneNumber)
            fragment.setArguments(args)
            return fragment
        }
    }
}
