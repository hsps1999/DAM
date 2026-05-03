# 03 — Screens

## Phase 1 — Ecrã único

### Main Screen (`MainActivity`)

Único ecrã da fase base. Apresenta uma lista de imagens de cães e permite atualizar.

**Componentes:**

| Componente         | Tipo                  | Função                                          |
| ------------------ | --------------------- | ----------------------------------------------- |
| Toolbar            | `MaterialToolbar`     | Título da app ("CatsNDogs")                    |
| RecyclerView       | `RecyclerView`        | Lista vertical de imagens                       |
| Item layout        | `CardView` + `ImageView` | Cada célula da lista                         |
| Refresh action     | `FloatingActionButton`| Recarrega a lista a partir da API               |

**Layout:**

- Root: `CoordinatorLayout`
- Toolbar fixa no topo
- RecyclerView ocupa o espaço restante
- FAB ancorado ao canto inferior direito

**Estado inicial:** lista vazia → dispara `fetchImages(20)` no `onCreate`.

## Phase 2 — Ecrãs adicionais

### Image Details Screen (`ImageDetailsActivity`)

Ativada ao tocar numa imagem da lista principal ou de favoritos.

**Componentes:**

| Componente         | Tipo                  | Função                                          |
| ------------------ | --------------------- | ----------------------------------------------- |
| Toolbar            | `MaterialToolbar`     | Botão "back" + título com a raça                |
| ImageView          | `ImageView`           | Imagem em tamanho grande                        |
| TextView (raça)    | `TextView`            | Raça e subraça                                  |
| TextView (id)      | `TextView`            | ID derivado do URL                              |
| TextView (url)     | `TextView`            | URL completo (selecionável)                     |
| Botão Favorite     | `MaterialButton`      | Toggle de favorito (ícone estrela cheia/vazia)  |

**Recebe via `Intent`:** o `id` do `ImageItem`. O ViewModel resolve o resto.

### Favorites Bar (componente partilhado)

Não é um ecrã, mas um componente acessível em todos os ecrãs (Phase 2).

**Implementação:** `BottomNavigationView` ou `LinearLayout` horizontal fixo no fundo da `Toolbar`, com até 5 miniaturas circulares dos favoritos atuais. Tocar numa miniatura abre o `ImageDetailsActivity` correspondente.

## Estados visuais

Cada ecrã que carrega dados deve suportar estes três estados:

1. **Loading** — `ProgressBar` central visível, lista oculta.
2. **Content** — lista visível, ProgressBar oculto.
3. **Error** — `Snackbar` com mensagem + botão "Retry"; lista mostra cache se existir.

## Suporte a orientações

- Portrait: lista vertical de 1 coluna
- Landscape: grid de 2 colunas (`GridLayoutManager` com `spanCount` baseado em recursos)

Definir `spanCount` em `res/values/integers.xml` e `res/values-land/integers.xml`.
