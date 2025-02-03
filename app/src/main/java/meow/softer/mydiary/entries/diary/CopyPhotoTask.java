package meow.softer.mydiary.entries.diary;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.FileManager;
import meow.softer.mydiary.shared.photo.BitmapHelper;
import meow.softer.mydiary.shared.photo.ExifUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyPhotoTask extends AsyncTask<Void,Void,String> {
    public interface CopyPhotoCallBack {
        void onCopyCompiled(String fileName);
    }

    private Uri uri;
    private String srcFileName;
    private ProgressDialog progressDialog;
    private CopyPhotoTask.CopyPhotoCallBack callBack;
    private Context mContext;
    private int reqWidth, reqHeight;
    private FileManager fileManager;
    private boolean isAddPicture = false;


    /**
     * From select image
     */
    public CopyPhotoTask(Context context, Uri uri,
                         int reqWidth, int reqHeight,
                         FileManager fileManager, CopyPhotoCallBack callBack) {
        this.uri = uri;
        isAddPicture = false;
        initTask(context, reqWidth, reqHeight, fileManager, callBack);

    }


    /**
     * From take a picture
     */
    public CopyPhotoTask(Context context, String srcFileName,
                         int reqWidth, int reqHeight,
                         FileManager fileManager, CopyPhotoCallBack callBack) {
        this.srcFileName = fileManager.getDirAbsolutePath() + "/" + srcFileName;
        isAddPicture = true;
        initTask(context, reqWidth, reqHeight, fileManager, callBack);
    }

    public void initTask(Context context,
                         int reqWidth, int reqHeight,
                         FileManager fileManager, CopyPhotoCallBack callBack) {
        this.mContext = context;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
        this.fileManager = fileManager;
        this.callBack = callBack;
        this.progressDialog = new ProgressDialog(context);

        progressDialog.setMessage(context.getString(R.string.process_dialog_loading));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar);
        progressDialog.show();
    }
    @Override
    protected String doInBackground(Void... voids) {
        String returnFileName = null;
        try {
            //1.Create bitmap
            //2.Get uri exif
            if (isAddPicture) {
                returnFileName = savePhotoToTemp(
                        ExifUtil.rotateBitmap(srcFileName,
                                BitmapHelper.getBitmapFromTempFileSrc(srcFileName, reqWidth, reqHeight)));
            } else {
                //rotateBitmap && resize
                returnFileName = savePhotoToTemp(
                        ExifUtil.rotateBitmap(mContext, uri,
                                BitmapHelper.getBitmapFromReturnedImage(mContext, uri, reqWidth, reqHeight)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CopyPhotoTask", e.toString());
        }
        return returnFileName;
    }

    @Override
    protected void onPostExecute(String srcFileName) {
        Log.e("Mytest","copyphoto onpost invoked");
        super.onPostExecute(srcFileName);
        progressDialog.dismiss();
        Log.e("Mytest","copyphoto onpost srcFileName: "+srcFileName);
        callBack.onCopyCompiled(srcFileName);
        Log.e("Mytest","copyphoto onpost callback invoked");
    }
    private String savePhotoToTemp(Bitmap bitmap) {
        //test start
        Log.e("Mytest","copyphoto savephoto input bitmap size: "+bitmap.getByteCount());
        //test end
        FileOutputStream out = null;
        String fileName = FileManager.createRandomFileName();
        try {
            out = new FileOutputStream(fileManager.getDirAbsolutePath() + "/" + fileName);
            Log.e("Mytest","copyphoto out path: "+fileManager.getDirAbsolutePath() + "/" + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        }catch (Exception e){
            Log.e("Mytest","copyphoto savephoto bitmap compress failed");
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //test
        Log.e("Mytest","copyphototask savephotototemp filename: "+fileName);
        if (fileName!=null){
            File file=new File(fileManager.getDirAbsolutePath() + "/" + fileName);
            long fileSize= file.length();
            Log.e("Mytest","copyphototask savephoto filelength: "+fileSize);
        }else {
            Log.e("Mytest","copyphototask savephoto file is null");
        }
        //test
        return fileName;
    }
}
