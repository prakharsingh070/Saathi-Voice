package com.example.saathi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saathi.domain.AssistantBrain
import com.example.saathi.domain.model.Scheme
import com.example.saathi.domain.repository.SchemeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SchemeResultsViewModel(
    private val schemeRepository: SchemeRepository,
    private val assistantBrain: AssistantBrain
) : ViewModel() {
    
    val schemes: StateFlow<List<Scheme>> = assistantBrain.relevantSchemes
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadSchemes(query: String = "") {
        // Now mostly handled by AssistantBrain, but can be used for direct search
        viewModelScope.launch {
            if (query.isNotBlank()) {
                _isLoading.value = true
                try {
                    val results = schemeRepository.searchSchemes(query)
                    // We don't update assistantBrain here to avoid circular logic
                    // This is just for the results page's own search if needed
                } catch (e: Exception) {
                    _error.value = "Network error."
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}
