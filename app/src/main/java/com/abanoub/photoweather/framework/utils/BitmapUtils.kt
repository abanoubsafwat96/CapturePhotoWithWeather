package com.abanoub.photoweather.framework.utils

import android.content.Context
import android.graphics.*
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

object BitmapUtils {

    fun writeTextOnDrawable(context: Context, bitmap: Bitmap, text: String): Bitmap {
        var bitmap = bitmap
        val typeface = Typeface.create("Helvetica", Typeface.BOLD)
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.YELLOW
        paint.typeface = typeface
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = convertToPixels(context, 45).toFloat()
        val textRect = Rect()
        paint.getTextBounds(text, 0, text.length, textRect)
        if (!bitmap.isMutable) {
            bitmap = convertToMutable(bitmap)
        }
        val canvas = Canvas(bitmap)

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= canvas.width - 4) //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.textSize =
                convertToPixels(context, 7).toFloat() //Scaling needs to be used for different dpi's

        //Calculate the positions
        val xPos = canvas.width / 2
        val yPos = (canvas.height * 0.1).toInt()
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)
        return bitmap
    }

    private fun convertToPixels(context: Context, nDP: Int): Int {
        val conversionScale = context.resources.displayMetrics.density
        return (nDP * conversionScale + 0.5f).toInt()
    }


    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    private fun convertToMutable(imgIn: Bitmap): Bitmap {
        var imgIn = imgIn
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            val file = File(
                Environment.getExternalStorageDirectory().toString() + File.separator + "temp.tmp"
            )

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            val randomAccessFile = RandomAccessFile(file, "rw")

            // get the width and height of the source bitmap.
            val width = imgIn.width
            val height = imgIn.height
            val type = imgIn.config

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            val channel = randomAccessFile.channel
            val map =
                channel.map(FileChannel.MapMode.READ_WRITE, 0, (imgIn.rowBytes * height).toLong())
            imgIn.copyPixelsToBuffer(map)
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle()
            System.gc() // try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type)
            map.position(0)
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map)
            //close the temporary file and channel , then delete that also
            channel.close()
            randomAccessFile.close()

            // delete the temp file
            file.delete()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imgIn
    }

    fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val mtx = Matrix()
        //       mtx.postRotate(degree);
        mtx.setRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
    }
}