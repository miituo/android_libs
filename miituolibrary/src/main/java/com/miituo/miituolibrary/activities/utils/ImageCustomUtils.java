package com.miituo.miituolibrary.activities.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class ImageCustomUtils {

    public static Bitmap getBitmap(Context context, Uri username){
        try {
            InputStream input = null;
            input = context.getContentResolver().openInputStream(username);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bit = BitmapFactory.decodeStream(input,null, options);
            input.close();

            File fdelete = new File(Objects.requireNonNull(getFilePath(username, context)));

            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :" );
                } else {
                    System.out.println("file not Deleted :");
                }
            }
            return bit;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getFilePath(Uri uri, Context context) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String picturePath = cursor.getString(columnIndex); // returns null
            cursor.close();
            return picturePath;
        }
        return null;
    }

    public static SpringForce animSpring(){
        SpringForce spring = new SpringForce(1.2f);
        spring.setStiffness(SpringForce.STIFFNESS_LOW);
        spring.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        return spring;
    }

    public static void launchImage(ImageView image){
        SpringAnimation springAnimX = new SpringAnimation(image, SpringAnimation.SCALE_X);
        SpringAnimation springAnimY = new SpringAnimation(image, SpringAnimation.SCALE_Y);
        springAnimX.setSpring(ImageCustomUtils.animSpring());
        springAnimY.setSpring(ImageCustomUtils.animSpring());
        springAnimX.start();
        springAnimY.start();
    }
}
