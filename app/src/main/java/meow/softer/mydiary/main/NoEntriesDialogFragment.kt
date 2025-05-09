package meow.softer.mydiary.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.shared.ThemeManager

class NoEntriesDialogFragment : DialogFragment(), View.OnClickListener {
    /**
     * UI
     */
    private var TV_no_entries_create: TextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        val rootView = inflater.inflate(R.layout.dialog_fragment_no_entries, container)

        TV_no_entries_create = rootView.findViewById<TextView?>(R.id.TV_no_entries_create)
        TV_no_entries_create!!.setOnClickListener(this)
        val content = SpannableString(TV_no_entries_create!!.getText())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        TV_no_entries_create!!.text = content
        TV_no_entries_create!!.setTextColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.TV_no_entries_create -> {
                val goEntriesPageIntent = Intent(activity, DiaryActivity::class.java)
                goEntriesPageIntent.putExtras(requireArguments())
                requireActivity().startActivity(goEntriesPageIntent)
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(topic: Long, diaryTitle: String?): NoEntriesDialogFragment {
            val args = Bundle()
            val fragment = NoEntriesDialogFragment()
            args.putLong("topicId", topic)
            args.putString("diaryTitle", diaryTitle)
            args.putBoolean("has_entries", false)
            fragment.setArguments(args)
            return fragment
        }
    }
}