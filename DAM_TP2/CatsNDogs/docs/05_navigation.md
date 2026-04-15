# 05 — Navigation

## Phase 1

Aplicação de ecrã único — sem navegação.

```
MainActivity (única)
```

## Phase 2

Navegação simples com duas Activities. Não é usado o Navigation Component (overhead desnecessário para 2 ecrãs); usa-se `Intent` direto.

```
        MainActivity
        ├── (tap em imagem da lista)
        │       └──> ImageDetailsActivity
        │
        └── (tap em miniatura da Favorites Bar)
                └──> ImageDetailsActivity
```

## Fluxos

### 1. Abrir detalhes a partir da lista principal

```
MainActivity
    └─ RecyclerView item click
        └─ Intent(ImageDetailsActivity).putExtra("imageId", item.id)
            └─ ImageDetailsActivity carrega item via ViewModel
```

### 2. Abrir detalhes a partir dos favoritos

Idêntico ao fluxo 1, mas o `id` vem da Favorites Bar em vez do RecyclerView principal.

### 3. Voltar a Main

`onBackPressed()` ou botão back da Toolbar (`setDisplayHomeAsUpEnabled(true)`).

### 4. Toggle de favorito (não muda de ecrã)

```
ImageDetailsActivity
    └─ Botão favorito clicado
        └─ ViewModel.toggleFavorite(id)
            └─ Repository atualiza Room
                └─ LiveData notifica observers (UI atualiza ícone)
                    └─ Favorites Bar em outras Activities reflete mudança no próximo onResume
```

## Comunicação entre Activities

- **Dados leves (id):** via `Intent` extras.
- **Estado partilhado (favoritos, cache):** via Room + LiveData. Cada Activity observa o seu próprio ViewModel, que partilha o mesmo Repository (singleton).

## Deep links

Não previstos nesta versão.

## Back stack

Comportamento padrão do Android — `ImageDetailsActivity` tem `MainActivity` como parent declarado no `AndroidManifest.xml`:

```xml
<activity
    android:name=".ui.details.ImageDetailsActivity"
    android:parentActivityName=".ui.main.MainActivity" />
```
