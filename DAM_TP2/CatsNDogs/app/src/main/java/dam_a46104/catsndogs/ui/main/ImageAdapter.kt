package dam_a46104.catsndogs.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dam_a46104.catsndogs.R
import dam_a46104.catsndogs.data.model.ImageItem

/**
 * Adapter para a lista principal de imagens de cães.
 *
 * Usa [ListAdapter] com [DiffUtil] para actualizações eficientes da lista.
 * O carregamento de imagens é delegado ao Glide.
 *
 * @param onItemClick Callback invocado quando o utilizador toca num item.
 *                    Recebe o [ImageItem] correspondente.
 */
class ImageAdapter(
    private val onItemClick: (ImageItem) -> Unit
) : ListAdapter<ImageItem, ImageAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder que mantém a referência ao [ImageView] de cada célula.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageViewDog)

        /**
         * Liga os dados de um [ImageItem] à View, carregando a imagem com Glide
         * e registando o click listener para abrir o ecrã de detalhe.
         */
        fun bind(item: ImageItem) {
            Glide.with(imageView.context)
                .load(item.url)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView)

            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    /**
     * Callback de comparação para o [DiffUtil].
     * Usa o [ImageItem.id] como chave de identidade e igualdade estrutural da data class.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<ImageItem>() {

        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean =
            oldItem == newItem
    }
}
