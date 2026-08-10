package com.example.saathi.data.repository

import com.example.saathi.domain.model.*
import com.example.saathi.domain.repository.HistoryRepository
import com.example.saathi.domain.repository.SchemeRepository
import com.example.saathi.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockSchemeRepository : SchemeRepository {
    private val schemes = listOf(
        Scheme(
            id = "1",
            name = "PM-KISAN",
            description = "Pradhan Mantri Kisan Samman Nidhi provide income support to all landholding farmers' families in the country.",
            relevance = 0.95f,
            eligibilityStatus = EligibilityStatus.LIKELY_ELIGIBLE,
            benefits = "₹6,000 per year in three installments of ₹2,000 each.",
            eligibilityCriteria = "Landholding farmers families with cultivable land in their names.",
            documents = listOf("Aadhaar Card", "Land records", "Bank account details"),
            steps = listOf("Visit official portal", "Register as new farmer", "Submit details"),
            sourceUrl = "https://pmkisan.gov.in",
            sourceOrganization = "Ministry of Agriculture",
            lastVerified = "2026-07-01",
            whyThis = "Based on your interest in farming and agriculture."
        ),
        Scheme(
            id = "2",
            name = "PMEGP (Business Loan)",
            description = "Credit-linked subsidy scheme for setting up new micro-enterprises.",
            relevance = 0.85f,
            eligibilityStatus = EligibilityStatus.MAY_BE_ELIGIBLE,
            benefits = "Subsidy of 15% to 35% on project cost.",
            eligibilityCriteria = "Any individual above 18 years of age. Self Help Groups are also eligible.",
            documents = listOf("Project report", "Aadhaar Card", "Education certificate"),
            steps = listOf("Apply online", "Bank review", "Training completion"),
            sourceUrl = "https://kviconline.gov.in/pmegpeportal",
            sourceOrganization = "Ministry of MSME",
            lastVerified = "2026-06-15",
            whyThis = "Recommended because you want to start a small business."
        ),
        Scheme(
            id = "3",
            name = "Ayushman Bharat (PM-JAY)",
            description = "World's largest health insurance scheme, providing cover of Rs. 5 lakhs per family per year for secondary and tertiary care hospitalization.",
            relevance = 0.90f,
            eligibilityStatus = EligibilityStatus.LIKELY_ELIGIBLE,
            benefits = "Cashless access to health care services (up to 5 lakhs) at empanelled public and private hospitals.",
            eligibilityCriteria = "Families listed in the Socio-Economic Caste Census (SECC) database.",
            documents = listOf("Aadhaar Card", "Ration Card", "PM Letter"),
            steps = listOf("Check eligibility online", "Visit empanelled hospital", "Contact Ayushman Mitra"),
            sourceUrl = "https://pmjay.gov.in",
            sourceOrganization = "National Health Authority",
            lastVerified = "2026-08-01",
            whyThis = "Found because you mentioned medical treatment, hospital, doctor or health issues."
        ),
        Scheme(
            id = "4",
            name = "Post Matric Scholarship",
            description = "Scholarships for SC/ST/OBC students to pursue higher education.",
            relevance = 0.88f,
            eligibilityStatus = EligibilityStatus.MAY_BE_ELIGIBLE,
            benefits = "Reimbursement of tuition fees and maintenance allowance.",
            eligibilityCriteria = "Student must be enrolled in recognized institution with family income below threshold.",
            documents = listOf("Caste Certificate", "Income Certificate", "Marksheet"),
            steps = listOf("Apply on National Scholarship Portal", "Institute verification", "State verification"),
            sourceUrl = "https://scholarships.gov.in",
            sourceOrganization = "Ministry of Social Justice",
            lastVerified = "2026-07-20",
            whyThis = "Recommended based on your query about education and studies."
        )
    )

    override suspend fun searchSchemes(query: String): List<Scheme> {
        val q = query.lowercase()
        return schemes.filter { scheme ->
            scheme.name.lowercase().contains(q) ||
            scheme.description.lowercase().contains(q) ||
            (q.contains("kisan") || q.contains("किसान") || q.contains("खेती") && scheme.id == "1") ||
            (q.contains("farmer") && scheme.id == "1") ||
            ((q.contains("business") || q.contains("startup") || q.contains("loan") || q.contains("shop") || q.contains("काम") || q.contains("बिज़नेस") || q.contains("दुकान") || q.contains("लोन")) && scheme.id == "2") ||
            ((q.contains("treatment") || q.contains("maa") || q.contains("health") || q.contains("hospital") || q.contains("doctor") || q.contains("bimari") || q.contains("ilaj") || q.contains("इलाज") || q.contains("बीमारी") || q.contains("अस्पताल") || q.contains("ट्रीटमेंट") || q.contains("मां")) && scheme.id == "3") ||
            ((q.contains("padhai") || q.contains("beti") || q.contains("scholarship") || q.contains("school") || q.contains("education") || q.contains("studies") || q.contains("पढ़ाई") || q.contains("बच्चे") || q.contains("बेटी") || q.contains("शिक्षा")) && scheme.id == "4")
        }
    }
    
    override suspend fun getSchemeById(id: String): Scheme? = schemes.find { it.id == id }
}

class MockUserRepository : UserRepository {
    private val profileFlow = MutableStateFlow(UserProfile())
    override fun getUserProfile(): Flow<UserProfile> = profileFlow.asStateFlow()
    override suspend fun updateUserProfile(profile: UserProfile) {
        profileFlow.value = profile
    }
    override suspend fun removeFact(fact: String) {
        val current = profileFlow.value
        profileFlow.value = current.copy(otherFacts = current.otherFacts.filter { it != fact })
    }
    override suspend fun clearRememberedData() {
        profileFlow.value = UserProfile()
    }
}

class MockHistoryRepository : HistoryRepository {
    private val messages = MutableStateFlow<List<Message>>(emptyList())
    private val sessions = MutableStateFlow<List<ConversationSession>>(listOf(
        ConversationSession(
            id = "s1",
            title = "SAATHI Interaction",
            timestamp = System.currentTimeMillis(),
            preview = "Assistant ready to help."
        )
    ))
    
    override fun getConversationHistory(): Flow<List<Message>> = messages.asStateFlow()
    override fun getSessions(): Flow<List<ConversationSession>> = sessions.asStateFlow()
    
    override suspend fun addMessage(message: Message) {
        messages.value = messages.value + message
    }
    
    override suspend fun clearHistory() {
        messages.value = emptyList()
        sessions.value = emptyList()
    }
}
