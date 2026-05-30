package com.competra.nfchelper

sealed class WriteChipResult {
    data class Success(val message: String) : WriteChipResult()
    data class Error(val message: String) : WriteChipResult()
}
