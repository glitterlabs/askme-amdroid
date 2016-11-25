package com.glitterlabs.videoqnaapp.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;

public class PickImage {

    public static int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    public static void pickImage(final Activity activity){

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //   boolean result=Utility.checkPermission(this);
                if (items[item].equals("Take Photo")) {
                   // userChoosenTask="Take Photo";
                    //  if(result)
                    cameraIntent(activity);
                } else if (items[item].equals("Choose from Library")) {
                  //  userChoosenTask="Choose from Library";
                    //     if(result)
                    galleryIntent(activity);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static void cameraIntent(Activity activity){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public static void galleryIntent(Activity activity){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
}
