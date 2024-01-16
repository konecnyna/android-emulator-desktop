package com.defkon.androidemulator.models

import java.io.File

data class AvdEntity(
        val name: String,
        val device: String,
        val path: File,
        val target: String,
        val sdCard: String
)