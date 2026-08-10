package com.example.saathi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saathi.domain.model.Scheme
import com.example.saathi.domain.repository.SchemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SchemeDetailsViewModel(private val schemeRepository: SchemeRepository) : ViewModel() {
    private val _scheme = MutableStateFlow<Scheme?>(null)
    val scheme = _scheme.asStateFlow()

    fun loadScheme(id: String) {
        viewModelScope.launch {
            _scheme.value = schemeRepository.getSchemeById(id)
        }
    }
}
