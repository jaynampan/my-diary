package meow.softer.mydiary.main

import android.content.Context
import android.os.Bundle
import android.view.View
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.gui.CommonDialogFragment

class TopicDeleteDialogFragment : CommonDialogFragment() {
    private var callback: DeleteCallback? = null

    /**
     * Callback
     */
    interface DeleteCallback {
        fun onTopicDelete(position: Int)
    }

    private var position = 0
    private var topicTitle: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as DeleteCallback
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.dialog!!.setCanceledOnTouchOutside(false)
        super.onViewCreated(view, savedInstanceState)
        position = requireArguments().getInt("position", -1)
        if (position == -1) {
            dismiss()
        }
        topicTitle = requireArguments().getString("topicTitle", "")
        this.TV_common_content?.text = resources.getString(R.string.topic_dialog_delete_content)+
                topicTitle
    }

    override fun okButtonEvent() {
        this.callback!!.onTopicDelete(position)
        dismiss()
    }

    override fun cancelButtonEvent() {
        dismiss()
    }

    companion object {
        fun newInstance(position: Int, topicTitle: String?): TopicDeleteDialogFragment {
            val args = Bundle()
            val fragment = TopicDeleteDialogFragment()
            args.putInt("position", position)
            args.putString("topicTitle", topicTitle)
            fragment.setArguments(args)
            return fragment
        }
    }
}