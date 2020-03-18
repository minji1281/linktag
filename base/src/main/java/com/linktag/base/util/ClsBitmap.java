package com.linktag.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClsBitmap {
    public static void setProfilePhoto(Context context, ImageView imageProfile, String fileFolder, String fileName, String preFilename, int nNoImage){
        imageProfile.setImageResource(nNoImage);

        if(fileName.equals("")){
//            deleteImageFile(context, "profile" + "/" + fileFolder, preFilename);
            deleteImageFolder(context, "profile" + "/" + fileFolder);

            imageProfile.setImageResource(nNoImage);
        } else {
            setImage(context, "profile" + "/" + fileFolder, fileName, preFilename, imageProfile, nNoImage);
        }
    }

    public static void setSharedPhoto(Context context, ImageView imageProfile, String fileFolder, String fileName, String preFilename, int nNoImage){
        imageProfile.setImageResource(nNoImage);

        if(fileName.equals("")){
//            deleteImageFile(context, "shared" + "/" + fileFolder, preFilename);
            deleteImageFolder(context, "shared" + "/" + fileFolder);

            imageProfile.setImageResource(nNoImage);
        } else {
            setImage(context, "shared" + "/" + fileFolder, fileName, preFilename, imageProfile, nNoImage);
        }
    }

    public static void setImage(final Context context, String folderPath, String fileName, String preFilename,
                                final ImageView imgView, int placeHolder) {
        try {
            final String strSavePath = context.getCacheDir() + "/" + folderPath;

            File dir = new File(strSavePath);
            if (!dir.exists()){
                dir.mkdirs();
            } else {
                if(preFilename.equals(""))
                    deleteImageFolder(context, folderPath);
                else
                    deleteImageFile(context, folderPath, preFilename);
            }

            final String strFilePath = strSavePath + "/" + fileName;

            File file = new File(strFilePath);

            if (file.exists()) {
                // Debug
                // file.delete();
            }

            if (file.exists()) {
                // 사진 파일을 받은 경우
                try {
                    CustomTarget target = new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imgView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    };

                    GlideApp.with(context).asBitmap().load(file).override(2000, 2000).into(target);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // 사진 파일을 받아야 하는 경우
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference pathRef = storageReference.child(folderPath + "/" + fileName);
                try {
                    CustomTarget target = new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imgView.setImageBitmap(resource);

                            makeImageFile(context, resource, strFilePath);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    };

                    GlideApp.with(context).asBitmap().load(pathRef).placeholder(placeHolder).into(target);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteImageFolder(Context context, String folderPath){
        final String strFolderPath = context.getCacheDir() + "/" + folderPath;

        File folder = new File(strFolderPath);
        try {
            File[] file_list = folder.listFiles();

            for(File childFile : file_list){
                childFile.delete();
            }

        } catch (Exception e){
            e.getStackTrace();
        }
    }

    public static void deleteImageFile(Context context, String folderPath, String fileName){
        final String strFilePath = context.getCacheDir() + "/" + folderPath + "/" + fileName;

        File file = new File(strFilePath);
        file.delete();
    }

    public static boolean makeImageFile(Context context, Bitmap bitmap, String path) {
        boolean bResult = true;
        if (bitmap == null)
            return false;

        // 폴더 확인
        File file = new File(path);

        FileOutputStream fileStream = null;

        try {
            fileStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileStream);
        } catch (FileNotFoundException e) {
            bResult = false;
            e.printStackTrace();
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bResult;
    }


}
