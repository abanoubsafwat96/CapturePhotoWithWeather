package com.abanoub.photoweather.framework.utils

import android.content.Context
import android.graphics.*

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
        val workingBitmap: Bitmap = Bitmap.createBitmap(imgIn)
        return workingBitmap.copy(Bitmap.Config.ARGB_8888, true)
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