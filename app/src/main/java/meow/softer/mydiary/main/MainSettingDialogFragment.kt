package meow.softer.mydiary.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.data.local.backup.BackupActivity
import meow.softer.mydiary.security.PasswordActivity
import meow.softer.mydiary.setting.SettingActivity
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.ThemeManager

class MainSettingDialogFragment : BottomSheetDialogFragment() {


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
        return rootView
    }
}