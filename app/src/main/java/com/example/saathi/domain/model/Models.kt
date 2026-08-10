package com.example.saathi.domain.model

data class Scheme(
    val id: String,
    val name: String,
    val description: String,
    val relevance: Float, // Relevance score from 0.0 to 1.0
    val eligibilityStatus: EligibilityStatus,
    val benefits: String,
    val eligibilityCriteria: String,
    val documents: List<String>,
    val steps: List<String>,
    val sourceUrl: String,
    val sourceOrganization: String,
    val lastVerified: String,
    val whyThis: String
)

data class UserProfile(
    val name: String = "",
    val age: String = "",
    val state: String = "",
    val district: String = "",
    val occupation: String = "",
    val incomeRange: String = "",
    val otherFacts: List<String> = emptyList()
)

data class Message(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ConversationSession(
    val id: String,
    val title: String,
    val timestamp: Long,
    val preview: String,
    val messages: List<Message> = emptyList()
)
