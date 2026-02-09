package com.github.kevinvane.clocklib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.github.kevinvane.clocklib.R
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
    }

    private val calendar = Calendar.getInstance()

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    // Default Colors
    private var colorFace = Color.WHITE
    private var colorHand = Color.parseColor("#1C1C1E")
    private var colorSecondHand = Color.parseColor("#FF9500")
    private var colorTick = Color.parseColor("#8E8E93")
    private var colorHourTick = Color.BLACK
    private var colorText = Color.BLACK

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClockView)
        colorFace = typedArray.getColor(R.styleable.ClockView_clockFaceColor, colorFace)
        colorHand = typedArray.getColor(R.styleable.ClockView_clockHandColor, colorHand)
        colorSecondHand = typedArray.getColor(R.styleable.ClockView_clockSecondHandColor, colorSecondHand)
        colorTick = typedArray.getColor(R.styleable.ClockView_clockTickColor, colorTick)
        colorHourTick = typedArray.getColor(R.styleable.ClockView_clockHourTickColor, colorHourTick)
        colorText = typedArray.getColor(R.styleable.ClockView_clockTextColor, colorText)
        typedArray.recycle()

        textPaint.color = colorText
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        val minDim = w.coerceAtMost(h)
        radius = (minDim / 2f) * 0.95f
        textPaint.textSize = radius * 0.18f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calendar.timeInMillis = System.currentTimeMillis()

        // 1. Draw Background Circle
        paint.color = colorFace
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 2. Draw Ticks
        drawClockFace(canvas)

        // 3. Draw Numbers
        drawNumbers(canvas)

        // 4. Draw Hands
        drawHands(canvas)

        postInvalidateOnAnimation()
    }

    private fun drawClockFace(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        val tickOuterRadius = radius * 0.98f

        for (i in 0 until 60) {
            val angle = Math.toRadians(i * 6.0).toFloat()
            val startX: Float
            val startY: Float
            val stopX = centerX + tickOuterRadius * sin(angle)
            val stopY = centerY - tickOuterRadius * cos(angle)

            if (i % 5 == 0) {
                paint.color = colorHourTick
                paint.strokeWidth = 8f
                val tickLength = radius * 0.05f
                startX = centerX + (tickOuterRadius - tickLength) * sin(angle)
                startY = centerY - (tickOuterRadius - tickLength) * cos(angle)
            } else {
                paint.color = colorTick
                paint.strokeWidth = 3f
                val tickLength = radius * 0.04f
                startX = centerX + (tickOuterRadius - tickLength) * sin(angle)
                startY = centerY - (tickOuterRadius - tickLength) * cos(angle)
            }
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        val numberRadius = radius * 0.75f
        val fontMetrics = textPaint.fontMetrics
        val textOffset = (fontMetrics.descent + fontMetrics.ascent) / 2f

        for (i in 1..12) {
            val angle = Math.toRadians(i * 30.0).toFloat()
            val x = centerX + numberRadius * sin(angle)
            val y = centerY - numberRadius * cos(angle) - textOffset
            canvas.drawText(i.toString(), x, y, textPaint)
        }
    }

    private fun drawHands(canvas: Canvas) {
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        val millis = calendar.get(Calendar.MILLISECOND)

        val smoothSeconds = seconds + millis / 1000f
        val smoothMinutes = minutes + smoothSeconds / 60f
        val smoothHours = hours + smoothMinutes / 60f

        // Hour Hand
        val hourAngle = Math.toRadians(smoothHours * 30.0).toFloat()
        drawHand(canvas, hourAngle, radius * 0.5f, 16f, colorHand, tailLength = radius * 0.08f)

        // Minute Hand
        val minuteAngle = Math.toRadians(smoothMinutes * 6.0).toFloat()
        drawHand(canvas, minuteAngle, radius * 0.75f, 10f, colorHand, tailLength = radius * 0.08f)

        // Second Hand
        val secondAngle = Math.toRadians(smoothSeconds * 6.0).toFloat()
        drawHand(canvas, secondAngle, radius * 0.88f, 4f, colorSecondHand, tailLength = radius * 0.15f)

        // Center Dot
        paint.color = colorHand
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, 10f, paint)

        paint.color = colorSecondHand
        canvas.drawCircle(centerX, centerY, 4f, paint)
    }

    private fun drawHand(
        canvas: Canvas,
        angle: Float,
        length: Float,
        width: Float,
        color: Int,
        tailLength: Float = 0f
    ) {
        paint.color = color
        paint.strokeWidth = width

        val startX = centerX - tailLength * sin(angle)
        val startY = centerY + tailLength * cos(angle)

        val stopX = centerX + length * sin(angle)
        val stopY = centerY - length * cos(angle)

        canvas.drawLine(startX, startY, stopX, stopY, paint)
    }
}