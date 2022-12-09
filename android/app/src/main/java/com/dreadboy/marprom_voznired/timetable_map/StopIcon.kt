package com.dreadboy.marprom_voznired.timetable_map

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.math.min

class StopIcon(private val color2: Int) : Drawable() {
    private val paint: Paint = Paint().apply { color = color2 }
    private val black: Paint = Paint().apply { setARGB(255, 0, 0, 0) }

    override fun draw(canvas: Canvas) {
        val width: Int = bounds.width()
        val height: Int = bounds.height()
        val radius: Float = min(width, height).toFloat() / 2f

        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, black)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius - 1, paint)
    }

    override fun setAlpha(alpha: Int) {
        // This method is required
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // This method is required
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.OPAQUE", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int =
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        PixelFormat.OPAQUE

    override fun getIntrinsicWidth(): Int = 32

    override fun getIntrinsicHeight(): Int = 32

    @Composable
    fun toBitmapDescriptor(): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(
            this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
