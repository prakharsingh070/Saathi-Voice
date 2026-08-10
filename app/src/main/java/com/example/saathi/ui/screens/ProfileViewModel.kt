package com.example.saathi.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saathi.domain.model.UserProfile
import com.example.saathi.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    val userProfile: StateFlow<UserProfile> = userRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.Lazily, UserProfile())

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            userRepository.updateUserProfile(profile)
        }
    }
    
    fun removeFact(fact: String) {
        viewModelScope.launch {
            userRepository.removeFact(fact)
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            userRepository.clearRememberedData()
        }
    }
}
