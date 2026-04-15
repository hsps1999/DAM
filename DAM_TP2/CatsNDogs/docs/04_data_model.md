# 04 — Data Model

## Entidades principais

### `ImageItem`

Representa uma imagem de cão obtida da API. É a unidade central da aplicação.

| Campo        | Tipo      | Descrição                                                  |
| ------------ | --------- | ---------------------------------------------------------- |
| `id`         | `String`  | Identificador único (derivado do nome do ficheiro do URL)  |
| `url`        | `String`  | URL completo da imagem                                     |
| `breed`      | `String`  | Raça principal (extraída do URL)                           |
| `subBreed`   | `String?` | Subraça, se existir (ex.: "afghan" em "hound-afghan")      |
| `isFavorite` | `Boolean` | Marca se está nos favoritos. Default: `false`              |
| `cachedAt`   | `Long`    | Timestamp Unix para política de cache (LRU)                |

**Definição Kotlin:**

```kotlin
data class ImageItem(
    val id: String,
    val url: String,
    val breed: String,
    val subBreed: String? = null,
    val isFavorite: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)
```

### `FavoriteEntry` (Phase 2)

Tabela Room separada para favoritos, garantindo a regra FIFO de máximo 5.

| Campo       | Tipo     | Descrição                                  |
| ----------- | -------- | ------------------------------------------ |
| `id`        | `String` | PK — mesmo `id` do `ImageItem`             |
| `url`       | `String` | URL da imagem                              |
| `breed`     | `String` | Raça                                       |
| `addedAt`   | `Long`   | Timestamp para ordenação FIFO              |

Regra de inserção: ao adicionar o 6.º favorito, eliminar o de menor `addedAt`.

### `CachedImage` (Phase 2)

Tabela Room para cache local (até 50 itens, excluindo favoritos).

| Campo       | Tipo     | Descrição                                  |
| ----------- | -------- | ------------------------------------------ |
| `id`        | `String` | PK                                         |
| `url`       | `String` | URL                                        |
| `breed`     | `String` | Raça                                       |
| `subBreed`  | `String?`| Subraça                                    |
| `cachedAt`  | `Long`   | Timestamp para política LRU                |

## Relações

- `ImageItem` é o modelo de domínio usado na UI e no ViewModel.
- `FavoriteEntry` e `CachedImage` são entidades de persistência (Room).
- O `Repository` faz o mapeamento entre entidades Room e `ImageItem`.

## Geração de `id`

O `id` é extraído do nome do ficheiro do URL para ser determinístico e estável entre sessões:

```
https://images.dog.ceo/breeds/pug/n02110958_15626.jpg
                                  ^^^^^^^^^^^^^^^^
                                  id = "n02110958_15626"
```

Isto permite verificar duplicados e identificar favoritos sem depender da ordem de chegada.
