package com.gustate.appbar

import android.content.Context

object Utils {

    private val Context.density: Float
        get() = resources.displayMetrics.density

    private val Context.fontScale: Float
        get() = resources.configuration.fontScale

    fun Float.dpToPx(context: Context): Float = this * context.density
    fun Float.spToPx(context: Context): Float = this * context.density * context.fontScale
    fun Float.pxToDp(context: Context): Float = this / context.density
    fun Float.pxToSp(context: Context): Float = this / (context.density * context.fontScale)

}