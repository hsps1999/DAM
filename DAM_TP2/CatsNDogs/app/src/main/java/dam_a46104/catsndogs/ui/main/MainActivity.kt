package dam_a46104.catsndogs.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.RecyclerView
import dam_a46104.catsndogs.R
import dam_a46104.catsndogs.data.remote.RetrofitClient
import dam_a46104.catsndogs.data.repository.ImageRepository
import dam_a46104.catsndogs.ui.common.UiState
import dam_a46104.catsndogs.viewmodel.MainViewModel

/**
 * Ecrã principal da aplicação.
 *
 * Apresenta uma lista de imagens de cães numa [RecyclerView] e permite
 * recarregar a lista através do FAB. Observa o [MainViewModel] para
 * reagir aos estados [UiState.Loading], [UiState.Success] e [UiState.Error].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabRefresh: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        setupFab()

        // Carrega imagens apenas na primeira criação da Activity
        if (savedInstanceState == null) {
            viewModel.loadImages()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupRecyclerView() {
        adapter = ImageAdapter()
        recyclerView = findViewById(R.id.recyclerViewImages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val repository = ImageRepository.getInstance(RetrofitClient.dogApiService)
        val factory = MainViewModel.Factory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.images.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Sem ProgressBar nesta fase — estado de loading silencioso
                }
                is UiState.Success -> {
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    Snackbar.make(recyclerView, state.message, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry)) { viewModel.loadImages() }
                        .show()
                }
            }
        }
    }

    private fun setupFab() {
        fabRefresh = findViewById(R.id.fabRefresh)
        fabRefresh.setOnClickListener {
            viewModel.loadImages()
        }
    }
}
