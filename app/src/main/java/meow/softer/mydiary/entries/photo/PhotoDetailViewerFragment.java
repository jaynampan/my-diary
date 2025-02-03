package meow.softer.mydiary.entries.photo;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import meow.softer.mydiary.R;

import me.relex.photodraweeview.PhotoDraweeView;

public class PhotoDetailViewerFragment extends Fragment {

    private PhotoDraweeView zoomImageView;
    private Uri photoUri;

    public static PhotoDetailViewerFragment newInstance(Uri photoUri) {
        Bundle args = new Bundle();
        PhotoDetailViewerFragment fragment = new PhotoDetailViewerFragment();
        args.putParcelable("photoUri", photoUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_photo_detail_viewer, container, false);
        zoomImageView = view.findViewById(R.id.zdv_photo_detail);
        photoUri = getArguments().getParcelable("photoUri");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initZoomView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initZoomView() {
        zoomImageView.setPhotoUri(photoUri);
    }
}
