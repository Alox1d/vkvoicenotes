package com.alox1d.vkvoicenotes.internal

import android.annotation.SuppressLint
import android.widget.TextView

import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
@BindingAdapter("formatTime")
fun formatTime(textView: TextView, timestamp: Long) {
    textView.setText(SimpleDateFormat("dd.MM.yy HH:mm").format(timestamp))
}