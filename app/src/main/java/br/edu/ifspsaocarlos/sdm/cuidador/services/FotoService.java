package br.edu.ifspsaocarlos.sdm.cuidador.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import br.edu.ifspsaocarlos.sdm.cuidador.callbacks.CallbackSimples;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ander on 26/10/2017.
 */

public abstract class FotoService {
    private static final String TAG = "FotoService";
    public static final int TAKE_PHOTO_CODE = 1;

    public abstract void run(File file);

    public static void corrigeRotacao(Context context, File file) {

        Bitmap bitmap = null;
        try {
            ExifInterface ei = new ExifInterface(file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file) );
            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotacionaImagem(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotacionaImagem(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotacionaImagem(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }

            salvaFoto(rotatedBitmap, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getTempFile(String packageName){
        //it will return /sdcard/image.tmp
        final File path = new File( Environment.getExternalStorageDirectory(), packageName );
        if(!path.exists()){
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }

    public void tirarFoto(Activity activity){
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(activity.getPackageName())) );
        activity.startActivityForResult(intent, TAKE_PHOTO_CODE);
    }

    private static void salvaFoto(Bitmap bitmap, File file) {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotacionaImagem(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void retornaFoto(int requestCode, int resultCode, Context context) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case TAKE_PHOTO_CODE:
                    corrigeRotacao(context, getTempFile(context.getPackageName()));
                    run(getTempFile(context.getPackageName()));
                    break;
            }
        }
    }

    public static void carregarAvatar(final CuidadorService service, String uri, final ImageView imageView, final CallbackSimples callback) {
        final File localFile;
        try {
            localFile = File.createTempFile("foto", ".jpg");
            service.carregaArquivo(Uri.parse(uri), localFile, new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    if(localFile.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imageView.setImageBitmap(bitmap);
                        callback.OnComplete();
                    }
                    Log.e("firebase ",";local tem file created  created " + localFile.toString());
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebase ",";local tem file not created  created " +exception.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}