package meow.softer.mydiary.entries.photo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.relex.photodraweeview.PhotoDraweeView
import meow.softer.mydiary.R

class PhotoDetailViewerFragment : Fragment() {
    private var zoomImageView: PhotoDraweeView? = null
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diary_photo_detail_viewer, container, false)
        zoomImageView = view.findViewById<PhotoDraweeView>(R.id.zdv_photo_detail)
        photoUri = requireArguments().getParcelable<Uri?>("photoUri")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initZoomView()
    }

    private fun initZoomView() {
        zoomImageView!!.setPhotoUri(photoUri)
    }

    companion object {
        fun newInstance(photoUri: Uri?): PhotoDetailViewerFragment {
            val args = Bundle()
            val fragment = PhotoDetailViewerFragment()
            args.putParcelable("photoUri", photoUri)
            fragment.setArguments(args)
            return fragment
        }
    }
}
