package com.example.intro_companion.backend.entities

/**
 * Data class representing an article.
 * @property title The title of the article.
 * @property description The description of the article.
 * @property url The URL of the article.
 */
data class Article(
    var title: String,
    var url: String
)