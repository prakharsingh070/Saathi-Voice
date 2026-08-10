package com.example.saathi.network

import com.example.saathi.domain.model.EligibilityStatus
import com.example.saathi.domain.model.Scheme

data class SchemeSearchRequest(
    val query: String,
    val userId: String?,
    val userProfile: Map<String, String>? = null
)

data class SchemeResponse(
    val id: String,
    val name: String,
    val description: String,
    val relevance: Float,
    val eligibility_status: String,
    val benefits: String,
    val eligibility_criteria: String,
    val documents: List<String>,
    val steps: List<String>,
    val source_url: String,
    val source_organization: String,
    val last_verified: String,
    val why_this: String
)

data class SearchResponse(
    val schemes: List<SchemeResponse>,
    val conversation_id: String?
)

data class HealthResponse(
    val status: String,
    val version: String
)

fun SchemeResponse.toDomain(): Scheme {
    return Scheme(
        id = id,
        name = name,
        description = description,
        relevance = relevance,
        eligibilityStatus = try {
            EligibilityStatus.valueOf(eligibility_status)
        } catch (e: Exception) {
            EligibilityStatus.UNKNOWN
        },
        benefits = benefits,
        eligibilityCriteria = eligibility_criteria,
        documents = documents,
        steps = steps,
        sourceUrl = source_url,
        sourceOrganization = source_organization,
        lastVerified = last_verified,
        whyThis = why_this
    )
}
