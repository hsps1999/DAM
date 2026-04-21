package dam_a46104.catsndogs.ui.details

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dam_a46104.catsndogs.CatsNDogsApp
import dam_a46104.catsndogs.R
import dam_a46104.catsndogs.data.model.ImageItem
import dam_a46104.catsndogs.data.remote.RetrofitClient
import dam_a46104.catsndogs.data.repository.ImageRepository
import dam_a46104.catsndogs.ui.common.FavoritesBarController
import dam_a46104.catsndogs.ui.common.UiState
import dam_a46104.catsndogs.viewmodel.DetailsViewModel

/**
 * Ecrã de detalhe de uma imagem.
 *
 * Recebe o [EXTRA_IMAGE_ID] via [android.content.Intent] e delega ao
 * [DetailsViewModel] a resolução do [ImageItem] a partir da cache em memória.
 * Apresenta a imagem em tamanho grande e os metadados: raça, id e URL.
 *
 * O back arrow navega para [dam_a46104.catsndogs.ui.main.MainActivity]
 * conforme declarado em `parentActivityName` no AndroidManifest.
 */
class ImageDetailsActivity : AppCompatActivity() {

    companion object {
        /** Chave do extra que transporta o id do [ImageItem] a apresentar. */
        const val EXTRA_IMAGE_ID = "extra_image_id"
    }

    private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_details)

        setupToolbar()
        setupViewModel()
        setupFavoritesBar()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarDetails)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViewModel() {
        val repository = ImageRepository.getInstance(
            RetrofitClient.dogApiService,
            (application as CatsNDogsApp).appDatabase
        )
        val factory = DetailsViewModel.Factory(application, repository)
        viewModel = ViewModelProvider(this, factory)[DetailsViewModel::class.java]

        viewModel.imageDetail.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Transição síncrona em memória — estado de loading praticamente instantâneo
                }
                is UiState.Success -> renderDetail(state.data)
                is UiState.Error -> {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        state.message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Observar estado de favorito → actualizar texto do botão
        viewModel.isFavorite.observe(this) { isFav ->
            findViewById<MaterialButton>(R.id.buttonFavorite).setText(
                if (isFav) R.string.favorite_remove else R.string.favorite_add
            )
        }

        // Click do botão de favorito
        findViewById<MaterialButton>(R.id.buttonFavorite).setOnClickListener {
            viewModel.toggleFavorite()
        }

        // Ler o id do Intent; fechar a Activity se estiver em falta
        val imageId = intent.getStringExtra(EXTRA_IMAGE_ID) ?: run {
            finish()
            return
        }
        viewModel.loadImage(imageId)
    }

    /**
     * Preenche todas as Views com os dados do [ImageItem] recebido.
     *
     * @param item O item de imagem a apresentar.
     */
    private fun renderDetail(item: ImageItem) {
        val breedLabel = item.breed.replaceFirstChar { it.uppercase() }
        val subBreedSuffix = item.subBreed?.let { " — ${it.replaceFirstChar { c -> c.uppercase() }}" } ?: ""

        // Título da Toolbar com a raça
        supportActionBar?.title = breedLabel

        // ImageView — carregado com Glide
        Glide.with(this)
            .load(item.url)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(findViewById(R.id.imageViewDetail))

        // TextViews de metadados
        findViewById<TextView>(R.id.textViewBreed).text = "$breedLabel$subBreedSuffix"
        findViewById<TextView>(R.id.textViewId).text = item.id
        findViewById<TextView>(R.id.textViewUrl).text = item.url
    }

    /** Delega o "back" ao dispatcher para compatibilidade com Predictive Back. */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /** Inicializa a barra de favoritos e liga o [FavoritesBarController]. */
    private fun setupFavoritesBar() {
        FavoritesBarController(
            barView = findViewById(R.id.favoritesBarView),
            lifecycleOwner = this,
            favoritesLiveData = viewModel.favorites,
            onFavClick = { imageId ->
                // Reutiliza esta Activity com o novo id (evita stack excessivo)
                intent.putExtra(EXTRA_IMAGE_ID, imageId)
                viewModel.loadImage(imageId)
            }
        )
    }
}
