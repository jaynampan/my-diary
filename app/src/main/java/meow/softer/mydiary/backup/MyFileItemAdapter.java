package meow.softer.mydiary.backup;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import meow.softer.mydiary.R;

import java.util.List;

public class MyFileItemAdapter extends RecyclerView.Adapter<MyFileItemAdapter.FileItemViewHolder> {
    private List<String> mDirNames;

    public MyFileItemAdapter(List<String> dirNames) {
        this.mDirNames = dirNames;
        Log.e("Mytest","mDirNames:"+mDirNames);
    }

    @NonNull
    @Override
    public FileItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_file_item, parent, false);
        return new FileItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileItemViewHolder holder, int position) {
        holder.tv_item_name.setText(mDirNames.get(position));
    }

    @Override
    public int getItemCount() {
        Log.e("Mytest",String.valueOf(mDirNames.size()));
        return mDirNames.size();
    }

    protected class FileItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_file_item;
        private ImageView iv_item_icon;
        private TextView tv_item_name;

        public FileItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ll_file_item = itemView.findViewById(R.id.file_item_container);
            this.iv_item_icon = itemView.findViewById(R.id.file_item_icon);
            this.tv_item_name = itemView.findViewById(R.id.file_item_name);
        }
    }
}
