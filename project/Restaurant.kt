package com.example.project.model

data class Restaurant(
    val id: String, // 문서 ID 추가
    val name: String,
    val address: String,
    val phoneNumber: String,
    val menu: List<String>,
    val congestion: Int,
    val image: Int
)
