package meow.softer.mydiary.entries.photo

import android.net.Uri
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PhotoDetailPagerAdapter(
    fm: FragmentManager,
    private val diaryPhotoFileList: ArrayList<Uri?>
) : FragmentPagerAdapter(fm) {
    private val registeredFragments = SparseArray<Fragment?>()

    override fun getItem(position: Int): PhotoDetailViewerFragment {
        val fragment =
            PhotoDetailViewerFragment.newInstance(diaryPhotoFileList[position])
        return fragment
    }

    override fun getCount(): Int {
        return diaryPhotoFileList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    fun getRegisteredFragment(position: Int): Fragment? {
        return registeredFragments.get(position)
    }
}
