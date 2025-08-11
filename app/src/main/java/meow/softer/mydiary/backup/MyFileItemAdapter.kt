package meow.softer.mydiary.backup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.MyFileItemAdapter.FileItemViewHolder

class MyFileItemAdapter(dirNames: MutableList<String?>) :
    RecyclerView.Adapter<FileItemViewHolder?>() {
    private val mDirNames: MutableList<String?> = dirNames

    init {
        Log.e("Mytest", "mDirNames:$mDirNames")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_file_item, parent, false)
        return FileItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        holder.tv_item_name.text = mDirNames[position]
    }

    override fun getItemCount(): Int {
        Log.e("Mytest", mDirNames.size.toString())
        return mDirNames.size
    }

     inner class FileItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ll_file_item: LinearLayout? = itemView.findViewById<LinearLayout?>(R.id.file_item_container)
         private val iv_item_icon: ImageView? = itemView.findViewById<ImageView?>(R.id.file_item_icon)
         val tv_item_name: TextView

        init {
            this.tv_item_name = itemView.findViewById<TextView>(R.id.file_item_name)
        }
    }
}
