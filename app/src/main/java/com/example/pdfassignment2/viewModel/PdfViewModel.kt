package com.example.pdfassignment2.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

//
//@HiltViewModel
//class PdfViewModel @Inject constructor() : ViewModel() {
//    private val _loadingState = MutableStateFlow(false)
//    val loadingState: StateFlow<Boolean> = _loadingState
//
//    private val _errorState = MutableStateFlow<String?>(null)
//    val errorState: StateFlow<String?> = _errorState
//
//    private val _pdfUrl = MutableStateFlow<String?>(null)
//    val pdfUrl: StateFlow<String?> = _pdfUrl
//
//    fun loadPdf(url: String) {
//        _errorState.value = null // clear previous errors on new load
//        _pdfUrl.value = url
//        Log.e("showPDF", "loadPdf: $url")
//    }
//
//    fun setLoading(isLoading: Boolean) {
//        _loadingState.value = isLoading
//        Log.e("showPDF", "setLoading: $isLoading")
//    }
//
//    fun setError(error: String) {
//        _errorState.value = error
//        Log.e("showPDF", "setError: $error")
//    }
//}



@HiltViewModel
class PdfViewModel @Inject constructor() : ViewModel() {
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    fun loadPdf(url: String) {
        _errorState.value = null
        _loadingState.value = true // ✅ Force loading state before anything starts
        _pdfUrl.value = url
        Log.e("showPDF", "loadPdf: $url")
    }

    fun setLoading(isLoading: Boolean) {
        _loadingState.value = isLoading
        Log.e("showPDF", "setLoading: $isLoading")
    }

    fun setError(error: String) {
        _errorState.value = error
        Log.e("showPDF", "setError: $error")
    }
}




// show pdf by using built-in pdf viewer

//@HiltViewModel
//class PdfViewModel @Inject constructor(
//    private val app: Application
//) : AndroidViewModel(app) {
//
//    val pdfFileLiveData = MutableLiveData<File>()
//
//    fun downloadPdf(url: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val connection = URL(url).openConnection() as HttpURLConnection
//                connection.connect()
//
//                val file = File(app.cacheDir, "downloaded.pdf")
//                val inputStream = connection.inputStream
//                val outputStream = FileOutputStream(file)
//
//                inputStream.copyTo(outputStream)
//
//                inputStream.close()
//                outputStream.close()
//
//                pdfFileLiveData.postValue(file)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}


