package dam_a46104.sketch2art.ui.main

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import dam_a46104.sketch2art.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val imageLoader by lazy {
        ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:100.0) Gecko/100.0 Firefox/100.0")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            }
            .crossfade(true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnClear.setOnClickListener {
            binding.drawingView.clear()
            binding.drawingView.visibility = View.VISIBLE
            binding.resultImage.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
            viewModel.resetState()
        }

        binding.btnGenerate.setOnClickListener {
            val bitmap = binding.drawingView.getBitmap()
            binding.emptyState.visibility = View.GONE
            viewModel.generateArt(bitmap)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainUiState.Idle -> {
                            binding.loadingOverlay.visibility = View.GONE
                            binding.btnGenerate.isEnabled = true
                        }
                        is MainUiState.Loading -> {
                            binding.loadingOverlay.visibility = View.VISIBLE
                            binding.btnGenerate.isEnabled = false
                        }
                        is MainUiState.Success -> {
                            binding.loadingOverlay.visibility = View.GONE
                            binding.btnGenerate.isEnabled = true
                            
                            binding.drawingView.visibility = View.GONE
                            binding.resultImage.visibility = View.VISIBLE
                            
                            Log.d("FAL_RESULT", "Processing Success: ${state.imageUrl}")
                            
                            // Robust image source handling: decode Base64 if it's a data URI
                            val imageData: Any = if (state.imageUrl.startsWith("http")) {
                                state.imageUrl
                            } else if (state.imageUrl.contains("base64,")) {
                                try {
                                    val base64Data = state.imageUrl.substringAfter("base64,")
                                    Base64.decode(base64Data, Base64.DEFAULT)
                                } catch (e: Exception) {
                                    Log.e("FAL_RESULT", "Failed to decode base64 result", e)
                                    state.imageUrl
                                }
                            } else {
                                state.imageUrl
                            }

                            val request = ImageRequest.Builder(this@MainActivity)
                                .data(imageData)
                                .target(binding.resultImage)
                                .crossfade(true)
                                .placeholder(android.R.drawable.progress_horizontal)
                                .error(android.R.drawable.stat_notify_error)
                                .transformations(RoundedCornersTransformation(16f))
                                .listener(
                                    onStart = { Log.d("COIL_DEBUG", "Loading started") },
                                    onSuccess = { _, _ -> Log.d("COIL_DEBUG", "Loading success!") },
                                    onError = { _, result -> 
                                        Log.e("COIL_ERROR", "Loading failed: ${result.throwable.message}")
                                    }
                                )
                                .build()
                            
                            imageLoader.enqueue(request)
                        }
                        is MainUiState.Error -> {
                            binding.loadingOverlay.visibility = View.GONE
                            binding.btnGenerate.isEnabled = true
                            binding.emptyState.visibility = View.VISIBLE
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
