package dam_a46104.catsndogs.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.RecyclerView
import dam_a46104.catsndogs.CatsNDogsApp
import dam_a46104.catsndogs.R
import dam_a46104.catsndogs.core.remote.RetrofitClient
import dam_a46104.catsndogs.core.repository.ImageRepository
import dam_a46104.catsndogs.ui.common.FavoritesBarController
import dam_a46104.catsndogs.core.common.UiState
import dam_a46104.catsndogs.ui.details.ImageDetailsActivity
import dam_a46104.catsndogs.viewmodel.MainViewModel

/**
 * Ecrã principal da aplicação.
 *
 * Apresenta uma lista de imagens de cães numa [RecyclerView] e permite
 * recarregar a lista através do FAB. Observa o [MainViewModel] para
 * reagir aos estados [UiState.Loading], [UiState.Success] e [UiState.Error].
 *
 * Durante [UiState.Loading], a [ProgressBar] é mostrada e o [RecyclerView]
 * é ocultado. Nos restantes estados, a situação inverte-se (mutuamente exclusivos).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ImageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupRecyclerView()
        setupProgressBar()
        setupViewModel()
        setupFab()
        setupFavoritesBar()

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
        adapter = ImageAdapter { item -> launchDetails(item.id) }
        recyclerView = findViewById(R.id.recyclerViewImages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    /** Inicializa a referência à [ProgressBar] do layout. */
    private fun setupProgressBar() {
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * Alterna entre ProgressBar e RecyclerView conforme o [UiState].
     * ProgressBar e RecyclerView são sempre mutuamente exclusivos:
     * nunca ambos visíveis ao mesmo tempo.
     */
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun setupViewModel() {
        val repository = ImageRepository.getInstance(
            RetrofitClient.dogApiService,
            (application as CatsNDogsApp).appDatabase
        )
        val factory = MainViewModel.Factory(application, repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.images.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    showLoading(true)
                }
                is UiState.Success -> {
                    showLoading(false)
                    adapter.submitList(state.data)
                    // Informar o utilizador que está a ver conteúdo guardado localmente
                    if (state.isFromCache) {
                        Snackbar.make(
                            recyclerView,
                            dam_a46104.catsndogs.core.R.string.info_offline_cache,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                is UiState.Error -> {
                    showLoading(false)
                    Snackbar.make(recyclerView, state.messageResId, Snackbar.LENGTH_LONG)
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

    /**
     * Lança o [ImageDetailsActivity] com o [imageId] do item tocado.
     *
     * @param imageId Identificador único da imagem, passado como extra no Intent.
     */
    private fun launchDetails(imageId: String) {
        val intent = Intent(this, ImageDetailsActivity::class.java)
            .putExtra(ImageDetailsActivity.EXTRA_IMAGE_ID, imageId)
        startActivity(intent)
    }

    /** Inicializa a barra de favoritos e liga o [FavoritesBarController]. */
    private fun setupFavoritesBar() {
        FavoritesBarController(
            barView = findViewById(R.id.favoritesBarView),
            lifecycleOwner = this,
            favoritesLiveData = viewModel.favorites,
            onFavClick = { imageId -> launchDetails(imageId) }
        )
    }
}
