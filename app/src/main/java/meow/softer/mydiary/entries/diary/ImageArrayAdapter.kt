package meow.softer.mydiary.entries.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ThemeManager

class ImageArrayAdapter(context: Context, private val images: Array<Int?>) : ArrayAdapter<Int?>(
    context, R.layout.spinner_imageview,
    images
) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val mContext: Context? = context

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_imageview, parent, false)
        }
        return getImageForPosition(position, convertView)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_imageview, parent, false)
        }
        return getImageForPosition(position, convertView)
    }

    private fun getImageForPosition(position: Int, rootView: View): View {
        val imageView = rootView.findViewById<ImageView>(R.id.IV_spinner)
        imageView.setImageResource(images[position]!!)
        imageView.setColorFilter(ThemeManager.instance!!.getThemeDarkColor(mContext!!))
        return rootView
    }
}
