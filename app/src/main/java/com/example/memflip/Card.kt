package com.example.memflip

class Card (
    val id: Int,
    val imageResId: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
    )