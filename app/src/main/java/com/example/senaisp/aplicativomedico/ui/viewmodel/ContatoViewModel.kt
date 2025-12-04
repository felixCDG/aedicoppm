package com.example.senaisp.aplicativomedico.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.senaisp.aplicativomedico.model.BuscarContato
import com.example.senaisp.aplicativomedico.repository.ContatoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val items: List<BuscarContato>) : UiState()
    data class Error(val message: String) : UiState()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ContatoViewModel(private val repository: ContatoRepository) : ViewModel() {

    private val query = MutableStateFlow("")

    private val _uiState: StateFlow<UiState> = query
        .debounce(300)
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { filtro ->
            if (filtro.isEmpty()) {
                flowOf<UiState>(UiState.Idle)
            } else {
                flow<UiState> {
                    emit(UiState.Loading)
                    val res = repository.buscarPorNome(filtro)
                    if (res.isSuccess) {
                        val list = res.getOrNull() ?: emptyList()
                        emit(UiState.Success(list))
                    } else {
                        emit(UiState.Error(res.exceptionOrNull()?.message ?: "Erro desconhecido"))
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Idle)

    fun uiState(): StateFlow<UiState> = _uiState

    fun setQuery(q: String) {
        query.value = q
    }

    // helper para reload atual
    fun refresh() {
        viewModelScope.launch {
            query.value = query.value // reemit
        }
    }
}
