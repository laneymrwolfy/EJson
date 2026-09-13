package com.example.model

data class RecentFile(
    val uriString: String,
    val fileName: String,
    val category: String = "Generic JSON",
    val lastOpened: Long = System.currentTimeMillis(),
    val sizeBytes: Long = 0L
)
