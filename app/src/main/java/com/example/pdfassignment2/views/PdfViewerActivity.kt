package com.example.pdfassignment2.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pdfassignment2.databinding.ActivityPdfViewerBinding
import com.example.pdfassignment2.viewModel.PdfViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.delay

@AndroidEntryPoint
class PdfViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewerBinding
    private val viewModel: PdfViewModel by viewModels()
    private var job: Job? = null

    companion object {
        const val EXTRA_PDF_URL = "pdf_url"

        fun start(context: Context, pdfUrl: String) {
            val intent = Intent(context, PdfViewerActivity::class.java).apply {
                putExtra(EXTRA_PDF_URL, pdfUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pdfUrl = intent.getStringExtra(EXTRA_PDF_URL) ?: run {
            showError("Invalid PDF URL provided")
            finish()
            return
        }

        setupWebView()
        setupObservers()

        // Clear WebView cache to force reload
        binding.webView.clearCache(true)
        binding.webView.clearHistory()

        // Load PDF
        viewModel.loadPdf(pdfUrl)
    }

    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false

            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.e("showPDF", "onPageStarted: $url")
                    if (url != null) {
                        if (url.contains("gview") || url.contains("googleusercontent")) {
                            viewModel.setLoading(true)
                        }
                    }
                }


                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.e("showPDF", "onPageFinished: $url")

                    // Only set loading false if it seems like a real document was loaded
                    if (url != null) {
                        if (url?.contains("gview") == true || url.contains("googleusercontent")) {
                            viewModel.setLoading(false)
                        }
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    viewModel.setLoading(false)
                    viewModel.setError("Failed to load PDF: ${error?.description}")
                }
            }
        }
    }

    private fun setupObservers() {
        job = lifecycleScope.launch {
            launch {
                viewModel.loadingState.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    binding.webView.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }
            }

            launch {
                viewModel.errorState.collect { error ->
                    if (error != null) {
                        binding.tvError.text = error
                        binding.tvError.visibility = View.VISIBLE
                        binding.webView.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    } else {
                        binding.tvError.visibility = View.GONE
                    }
                }
            }

            launch {
                viewModel.pdfUrl.collect { url ->
                    url?.let {
                        val googleDocsUrl =
                            "https://docs.google.com/gview?embedded=true&url=${Uri.encode(it)}"

                        // Ensure fresh load
                        binding.webView.loadUrl("about:blank")
                        delay(100) // Small delay to ensure UI updates
                        binding.webView.loadUrl(googleDocsUrl)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}


// show pdf by using built-in pdf viewer

//@AndroidEntryPoint
//class PdfViewerActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityPdfViewerBinding
//    private val viewModel: PdfViewModel by viewModels()
//    private lateinit var imageView: ImageView
//
//    private val pdfUrl =
//        "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        imageView = findViewById(R.id.pdfImageView)
//
//        // Observe LiveData for downloaded file
//        viewModel.pdfFileLiveData.observe(this) { file ->
//            file?.let {
//                showPdf(file)
//            }
//        }
//
//        // Start downloading
//        viewModel.downloadPdf(pdfUrl)
//    }
//
//    private fun showPdf(file: File) {
//        try {
//            val fileDescriptor =
//                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//            val renderer = PdfRenderer(fileDescriptor)
//            val page = renderer.openPage(0) // Show only first page
//
//            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            imageView.setImageBitmap(bitmap)
//
//            page.close()
//            renderer.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}

