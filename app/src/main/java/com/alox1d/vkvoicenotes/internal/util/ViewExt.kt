package com.alox1d.vkvoicenotes.internal.util

import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.rotateTo180(){
    this.animate().setDuration(200).rotation(180f)
}
fun FloatingActionButton.rotateToDefault(){
        this.animate().setDuration(200).rotation(0f)
}