package com.example.moviebooktracker

data class Item(
    val name: String,
    val genre: String,
    val desc: String,
    val score: Int,
    val type: String,
    val isDone: Boolean,
    val iconRes: Int
)
