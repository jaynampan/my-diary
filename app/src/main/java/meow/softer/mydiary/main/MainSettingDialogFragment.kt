package meow.softer.mydiary.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.BackupActivity
import meow.softer.mydiary.security.PasswordActivity
import meow.softer.mydiary.setting.SettingActivity
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.ThemeManager

class MainSettingDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {
    /**
     * UI
     */
    private var RL_main_setting_dialog: RelativeLayout? = null
    private var IV_main_setting_setting_page: ImageView? = null
    private var IV_main_setting_add_topic: ImageView? = null
    private var IV_main_setting_setting_security: ImageView? = null
    private var IV_main_setting_backup: ImageView? = null
    private var IV_main_setting_about: ImageView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.dialog!!.setCanceledOnTouchOutside(true)
        val rootView = inflater.inflate(R.layout.bottom_sheet_main_setting, container)
        val composeView = rootView.findViewById<ComposeView>(R.id.composeView)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MainSettingDialog(
                    ThemeManager.instance!!.getThemeColor(requireContext()),
                    hasPassword = (requireActivity().application as MyDiaryApplication).isHasPassword
                ) { it ->
                    when (it) {
                        "Add" -> {
                            val createTopicDialogFragment =
                                TopicDetailDialogFragment.newInstance(
                                    false,
                                    -1,
                                    -1,
                                    "",
                                    -1,
                                    Color.BLACK
                                )
                            createTopicDialogFragment.show(
                                requireFragmentManager(),
                                "createTopicDialogFragment"
                            )
                            dismiss()
                        }

                        "Setting" -> {
                            val settingPageIntent = Intent(activity, SettingActivity::class.java)
                            requireActivity().startActivity(settingPageIntent)
                            dismiss()
                        }

                        "Lock" -> {
                            val securityPageIntent = Intent(activity, PasswordActivity::class.java)
                            if ((requireActivity().application as MyDiaryApplication).isHasPassword) {
                                securityPageIntent.putExtra(
                                    "password_mode",
                                    PasswordActivity.REMOVE_PASSWORD
                                )
                            } else {
                                securityPageIntent.putExtra(
                                    "password_mode",
                                    PasswordActivity.CREATE_PASSWORD
                                )
                            }
                            requireActivity().startActivity(securityPageIntent)
                            dismiss()
                        }

                        "Backup" -> {
                            val backupIntent = Intent(activity, BackupActivity::class.java)
                            requireActivity().startActivity(backupIntent)
                            dismiss()
                        }

                        "About" -> {
                            val aboutPageIntent = Intent(activity, AboutActivity::class.java)
                            requireActivity().startActivity(aboutPageIntent)
                            dismiss()
                        }

                        else -> {}
                    }
                }
            }
        }
//        RL_main_setting_dialog = rootView.findViewById<RelativeLayout?>(R.id.RL_main_setting_dialog)
//        RL_main_setting_dialog!!.setBackgroundColor(
//            ThemeManager.instance!!.getThemeMainColor(requireContext())
////        )
//        IV_main_setting_setting_page =
//            rootView.findViewById<ImageView?>(R.id.IV_main_setting_setting_page)
//        IV_main_setting_setting_page!!.setOnClickListener(this)
//        IV_main_setting_add_topic =
//            rootView.findViewById<ImageView?>(R.id.IV_main_setting_add_topic)
//        IV_main_setting_add_topic!!.setOnClickListener(this)
//        IV_main_setting_setting_security =
//            rootView.findViewById<ImageView?>(R.id.IV_main_setting_setting_security)
//        IV_main_setting_setting_security!!.setOnClickListener(this)
//        IV_main_setting_backup = rootView.findViewById<ImageView?>(R.id.IV_main_setting_backup)
//        IV_main_setting_backup!!.setOnClickListener(this)
//        IV_main_setting_about = rootView.findViewById<ImageView?>(R.id.IV_main_setting_about)
//        IV_main_setting_about!!.setOnClickListener(this)


//        if ((requireActivity().application as MyDiaryApplication).isHasPassword) {
//            IV_main_setting_setting_security!!.setImageResource(R.drawable.ic_enhanced_encryption_white_36dp)
//        } else {
//            IV_main_setting_setting_security!!.setImageResource(R.drawable.ic_no_encryption_white_36dp)
//        }
        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.IV_main_setting_add_topic -> {
//                val createTopicDialogFragment =
//                    TopicDetailDialogFragment.newInstance(false, -1, -1, "", -1, Color.BLACK)
//                createTopicDialogFragment.show(requireFragmentManager(), "createTopicDialogFragment")
//                dismiss()
//            }
//
//            R.id.IV_main_setting_setting_page -> {
//                val settingPageIntent = Intent(activity, SettingActivity::class.java)
//               requireActivity().startActivity(settingPageIntent)
//                dismiss()
//            }
//
//            R.id.IV_main_setting_setting_security -> {
//                val securityPageIntent = Intent(activity, PasswordActivity::class.java)
//                if ((requireActivity().application as MyDiaryApplication).isHasPassword) {
//                    securityPageIntent.putExtra("password_mode", PasswordActivity.REMOVE_PASSWORD)
//                } else {
//                    securityPageIntent.putExtra("password_mode", PasswordActivity.CREATE_PASSWORD)
//                }
//               requireActivity().startActivity(securityPageIntent)
//                dismiss()
//            }
//
//            R.id.IV_main_setting_backup -> {
//                val backupIntent = Intent(activity, BackupActivity::class.java)
//               requireActivity().startActivity(backupIntent)
//                dismiss()
//            }
//
//            R.id.IV_main_setting_about -> {
//                val aboutPageIntent = Intent(activity, AboutActivity::class.java)
//                requireActivity().startActivity(aboutPageIntent)
//                dismiss()
//            }
        }
    }
}