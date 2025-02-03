package meow.softer.mydiary.entries.photo;

import android.net.Uri;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PhotoDetailPagerAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private ArrayList<Uri> diaryPhotoFileList;

    public PhotoDetailPagerAdapter(FragmentManager fm, ArrayList<Uri> diaryPhotoFileList) {
        super(fm);
        this.diaryPhotoFileList = diaryPhotoFileList;
    }

    @NonNull
    @Override
    public PhotoDetailViewerFragment getItem(int position) {
        PhotoDetailViewerFragment fragment =
                PhotoDetailViewerFragment.newInstance(diaryPhotoFileList.get(position));
        return fragment;
    }

    @Override
    public int getCount() {
        return diaryPhotoFileList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
