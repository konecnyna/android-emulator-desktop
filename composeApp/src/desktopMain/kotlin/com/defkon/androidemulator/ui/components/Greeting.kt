package com.defkon.androidemulator.ui.components

import com.defkon.androidemulator.utils.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}