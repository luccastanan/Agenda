package com.example.anderson.agenda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import java.io.ByteArrayOutputStream;

/**
 * Created by Luccas on 16/02/2015.
 */
public class Utilitarios {

    //Converte Bitmap em byte
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    //Converte byte em Bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    //Cria imagem redonda
    public static Bitmap getCircle(Bitmap scaleBitmapImage,int width,int height) {
        int targetWidth = width;
        int targetHeight = height;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    //Cria imagem quadrada
    public static Bitmap getSquare(Bitmap image) {
        Bitmap dstBmp;
        if (image.getWidth() >= image.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    image,
                    image.getWidth() / 2 - image.getHeight() / 2,
                    0,
                    image.getHeight(),
                    image.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    image,
                    0,
                    image.getHeight() / 2 - image.getWidth() / 2,
                    image.getWidth(),
                    image.getWidth()
            );
        }

        return dstBmp;
    }
}
