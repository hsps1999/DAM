package dam_a46104.catsndogs.ui.common

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import dam_a46104.catsndogs.R
import dam_a46104.catsndogs.data.model.ImageItem

/**
 * Controller que gere a barra horizontal de miniaturas de favoritos.
 *
 * Observa o [LiveData] de favoritos e actualiza os 5 slots de [ShapeableImageView]
 * com as miniaturas via Glide. Slots sem favorito ficam [View.GONE].
 *
 * Instanciar dentro de [androidx.appcompat.app.AppCompatActivity.onCreate] ou
 * [androidx.fragment.app.Fragment.onViewCreated], passando `this` como [lifecycleOwner].
 *
 * @param barView        View raiz da FavoritesBar (incluída via `<include>` no layout).
 * @param lifecycleOwner LifecycleOwner da Activity ou Fragment que aloja a barra.
 * @param favoritesLiveData LiveData com a lista actual de favoritos.
 * @param onFavClick     Callback invocado com o [ImageItem.id] quando se toca numa miniatura.
 */
class FavoritesBarController(
    barView: View,
    lifecycleOwner: LifecycleOwner,
    favoritesLiveData: LiveData<List<ImageItem>>,
    private val onFavClick: (String) -> Unit
) {

    /** Lista ordenada dos 5 ImageView slots da barra. */
    private val slots: List<ShapeableImageView> = listOf(
        barView.findViewById(R.id.imgFav1),
        barView.findViewById(R.id.imgFav2),
        barView.findViewById(R.id.imgFav3),
        barView.findViewById(R.id.imgFav4),
        barView.findViewById(R.id.imgFav5)
    )

    init {
        favoritesLiveData.observe(lifecycleOwner) { favorites ->
            bind(favorites ?: emptyList())
        }
    }

    /**
     * Actualiza os slots com os dados dos [favorites].
     * Slots com favorito ficam [View.VISIBLE] com miniatura circular via Glide.
     * Slots sem favorito ficam [View.GONE].
     *
     * @param favorites Lista actual de favoritos (máximo 5).
     */
    private fun bind(favorites: List<ImageItem>) {
        slots.forEachIndexed { index, imageView ->
            if (index < favorites.size) {
                val item = favorites[index]
                imageView.visibility = View.VISIBLE
                Glide.with(imageView.context)
                    .load(item.url)
                    .circleCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imageView)
                imageView.setOnClickListener { onFavClick(item.id) }
            } else {
                imageView.visibility = View.GONE
                imageView.setImageDrawable(null)
                imageView.setOnClickListener(null)
            }
        }
    }
}
