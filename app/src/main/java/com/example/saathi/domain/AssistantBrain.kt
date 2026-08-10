package com.example.saathi.domain

import com.example.saathi.data.local.QdrantMemoryService
import com.example.saathi.domain.model.EligibilityStatus
import com.example.saathi.domain.model.Scheme
import com.example.saathi.domain.repository.SchemeRepository
import com.example.saathi.domain.repository.UserRepository
import com.example.saathi.network.GroqService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssistantBrain(
    private val schemeRepository: SchemeRepository,
    private val userRepository: UserRepository,
    private val groqService: GroqService,
    private val qdrantMemory: QdrantMemoryService
) {

    private val _relevantSchemes = MutableStateFlow<List<Scheme>>(emptyList())
    val relevantSchemes: StateFlow<List<Scheme>> = _relevantSchemes.asStateFlow()

    private var activeScheme: Scheme? = null
    private var conversationContext = ""
    
    // User details captured in current session
    private var capturedState: String? = null
    private var capturedAge: String? = null
    private var capturedOccupation: String? = null

    suspend fun getResponse(userInput: String): AssistantResponse {
        val input = userInput.lowercase()
        
        // --- 1. SENSE: Extract info from natural speech ---
        extractUserInfo(input)
        qdrantMemory.saveFact(userInput, mapOf("type" to "user_input"))

        // --- 2. THINK: Call Groq with full historical context ---
        val fullContext = """
            $conversationContext
            [Current Knowledge: State=$capturedState, Age=$capturedAge, Job=$capturedOccupation]
        """.trimIndent()
        
        val aiResponse = groqService.getHumanResponse(userInput, fullContext)
        
        if (aiResponse.isNotBlank()) {
            conversationContext += "\nUser: $userInput\nSAATHI: $aiResponse"
            
            // DYNAMIC UPDATE: Sync Results tab from AI output
            val discoveredSchemes = parseSchemesFromAi(aiResponse)
            val localSchemes = identifyRelevantSchemes(userInput)
            val merged = (discoveredSchemes + localSchemes).distinctBy { it.name.lowercase() }
            
            if (merged.isNotEmpty()) {
                activeScheme = merged.first()
                _relevantSchemes.value = merged
            }

            val displayMessage = aiResponse.replace(Regex("\\[SCHEME_FOUND:.*?\\]"), "").trim()
            return AssistantResponse(text = displayMessage, speakingText = displayMessage)
        }

        // --- 3. FALLBACK: Human-like local expert ---
        val localSchemes = identifyRelevantSchemes(userInput)
        return if (localSchemes.isNotEmpty()) {
            activeScheme = localSchemes.first()
            _relevantSchemes.value = localSchemes
            val msg = "Ji, maine records check kiye hain. Aap '${activeScheme!!.name}' ke liye eligible ho sakte hain. Kya main iski details Results section mein dikhao?"
            AssistantResponse(msg, msg)
        } else {
            val msg = "Ji Namaste, main samajh gaya. Aap apni pareshani thoda aur vistaar mein bataiye taaki main sahi scheme dhund sakun."
            AssistantResponse(text = msg, speakingText = msg)
        }
    }

    private fun extractUserInfo(input: String) {
        if (input.contains("year") || input.contains("saal") || Regex("\\d{2}").containsMatchIn(input)) {
            capturedAge = Regex("\\d{2}").find(input)?.value ?: capturedAge
        }
        val states = listOf("up", "bihar", "punjab", "haryana", "delhi", "rajasthan", "maharashtra")
        states.forEach { if (input.contains(it)) capturedState = it }
        
        if (input.contains("kisan") || input.contains("farmer") || input.contains("kheti")) capturedOccupation = "Farmer"
        if (input.contains("business") || input.contains("loan") || input.contains("shop")) capturedOccupation = "Business"
    }

    private suspend fun identifyRelevantSchemes(input: String): List<Scheme> {
        val q = input.lowercase()
        return when {
            q.contains("kisan") || q.contains("किसान") -> schemeRepository.searchSchemes("kisan")
            q.contains("health") || q.contains("hospital") || q.contains("bimari") || q.contains("ilaj") -> schemeRepository.searchSchemes("ayushman")
            q.contains("padhai") || q.contains("studies") || q.contains("school") || q.contains("beti") -> schemeRepository.searchSchemes("scholarship")
            else -> schemeRepository.searchSchemes(input)
        }
    }

    private fun parseSchemesFromAi(aiText: String): List<Scheme> {
        val schemes = mutableListOf<Scheme>()
        val pattern = Regex("\\[SCHEME_FOUND: (.*?) \\| (.*?) \\| (.*?)\\]")
        val matches = pattern.findAll(aiText)
        matches.forEach { match ->
            val name = match.groupValues[1].trim()
            val url = match.groupValues[2].trim()
            val benefits = match.groupValues[3].trim()
            schemes.add(Scheme(id = "ai_${name.hashCode()}", name = name, description = "Real-time identification.", relevance = 0.99f, eligibilityStatus = EligibilityStatus.UNKNOWN, benefits = benefits, eligibilityCriteria = "Check official portal", documents = listOf("Aadhaar"), steps = listOf("Visit $url"), sourceUrl = url, sourceOrganization = "Govt", lastVerified = "Live", whyThis = "Match found by AI."))
        }
        return schemes
    }

    data class AssistantResponse(val text: String, val speakingText: String)
}
