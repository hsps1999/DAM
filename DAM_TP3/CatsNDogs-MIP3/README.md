# CatsNDogs 🐶

Aplicação Android que apresenta imagens aleatórias de cães a partir da [Dog CEO API](https://dog.ceo/dog-api/),
com suporte a favoritos persistidos, acesso offline e ecrã de detalhes por imagem.

Desenvolvido como exercício prático de DAM (Desenvolvimento de Aplicações Móveis) no ISEL,
seguindo uma abordagem *planning-first* com documentação Markdown e desenvolvimento assistido por IA.

---

## Funcionalidades

### Phase 1 — Base
- Lista de imagens aleatórias de cães (RecyclerView + Glide)
- Refresh via FAB
- Indicador de loading (ProgressBar)
- Tratamento de erros com Snackbar e botão "Retry"
- Ecrã de detalhes (raça, ID, URL)

### Phase 2 — Extensões
- **Cache local** com Room — até 50 imagens (LRU), persiste entre sessões
- **Acesso offline** — fallback automático para cache quando sem rede
- **Favoritos FIFO** — até 5 favoritos persistidos em Room; o mais antigo é removido ao adicionar o 6.º
- **Barra de favoritos** — miniaturas circulares visíveis em todos os ecrãs; toque lança o detalhe
- **Toggle de favorito** — botão no ecrã de detalhes para adicionar/remover

---

## Screenshots

> *(Imagens a adicionar após captura no emulador/dispositivo)*

| Ecrã Principal | Detalhe | Favoritos | Offline |
|:-:|:-:|:-:|:-:|
| ![Main](docs/screenshots/main.png) | ![Details](docs/screenshots/details.png) | ![Favorites](docs/screenshots/favorites.png) | ![Offline](docs/screenshots/offline.png) |

---

## Como correr o projeto

### Pré-requisitos
- Android Studio Hedgehog (2023.1) ou superior
- JDK 11+
- Emulador ou dispositivo com **Android 7.0+ (API 24)**

### Passos
```bash
git clone https://github.com/hsps1999/DAM.git
cd DAM/DAM_TP2/CatsNDogs
```

1. Abrir a pasta `CatsNDogs` no Android Studio (**File → Open**)
2. Aguardar o Gradle Sync
3. Selecionar um emulador/dispositivo
4. Premir **Run ▶**

Não é necessária nenhuma chave de API — a [Dog CEO API](https://dog.ceo/dog-api/) é pública e sem autenticação.

---

## Stack técnico

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Kotlin |
| UI | XML Views (Material Components 3) |
| Networking | Retrofit 2 + Gson |
| Concorrência | Kotlin Coroutines |
| Carregamento de imagens | Glide |
| Persistência | Room 2.7 (KSP 2) |
| Arquitetura | MVVM + Repository pattern |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## Estrutura do projeto

```
app/src/main/java/dam_a46104/catsndogs/
├── data/
│   ├── local/          # Room: CachedImage, FavoriteEntry, DAOs, AppDatabase
│   ├── model/          # ImageItem (modelo de domínio)
│   ├── remote/         # Retrofit: DogApiService, RetrofitClient
│   └── repository/     # ImageRepository (única fonte de verdade)
├── ui/
│   ├── common/         # UiState, FavoritesBarController
│   ├── details/        # ImageDetailsActivity
│   └── main/           # MainActivity, ImageAdapter
├── viewmodel/          # MainViewModel, DetailsViewModel
└── CatsNDogsApp.kt     # Application class (Room singleton)
```

Documentação de desenho em [`docs/`](docs/):

| Ficheiro | Conteúdo |
|----------|----------|
| `01_overview.md` | Propósito e stack |
| `02_features.md` | Funcionalidades detalhadas |
| `03_screens.md` | Especificação dos ecrãs |
| `04_data_model.md` | Modelos de dados |
| `05_navigation.md` | Navegação entre ecrãs |
| `06_architecture.md` | Camadas e estrutura de pastas |
| `07_api_usage.md` | Endpoints e mapeamento |
| `08_implementation_plan.md` | Plano de implementação Phase 1 |
| `09_feature_extensions.md` | Extensões Phase 2 |

---

## API

**Dog CEO Dog API** — [dog.ceo/dog-api](https://dog.ceo/dog-api/)

Endpoint utilizado:
```
GET https://dog.ceo/api/breeds/image/random/{count}
```

Devolve URLs de imagens aleatórias de cães. Sem autenticação. Sem rate limiting relevante para uso normal.

---

## Licença

Projeto académico — ISEL, Licenciatura em Engenharia Informática e de Computadores, 2025/26.
