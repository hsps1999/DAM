# JetpackWeatherApp — Aplicação Android de Meteorologia com Jetpack Compose

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26  
**Fase:** Trabalho Prático 3 (TP3)

---

## 1. Overview

A **JetpackWeatherApp** é uma reescrita da CoolWeatherApp (TP2) com arquitetura moderna: UI totalmente em **Jetpack Compose**, cliente HTTP assíncrono com **Ktor** e estado gerido via **StateFlow**. Além das condições atuais, apresenta sensação térmica, humidade, visibilidade e índice UV, e permite guardar localizações favoritas com persistência entre sessões.

---

## 2. Arquitetura do Sistema

**Linguagem:** Kotlin  
**Min SDK:** API 24 (Android 7.0 Nougat)  
**Build System:** Gradle (Kotlin DSL)  
**Package:** `dam_a46104.jetpackweatherapp`  
**UI Engine:** Jetpack Compose

```
JetpackWeatherApp/
└── app/src/main/java/dam_a46104/jetpackweatherapp/
    ├── MainActivity.kt                  → Ponto de entrada, setContent com JetpackWeatherAppTheme
    ├── data/
    │   ├── WeatherApiClient.kt          → Ktor HTTP (suspend), Open-Meteo API
    │   ├── WeatherData.kt               → Data classes com @Serializable (kotlinx.serialization)
    │   ├── WmoWeatherCode.kt            → Enum WMO codes → nomes de drawables (day/night)
    │   ├── FavoriteLocation.kt          → Data class de localização favorita
    │   └── FavoritesRepository.kt       → CRUD sobre SharedPreferences (JSON serializado)
    ├── ui/
    │   ├── WeatherScreen.kt             → UI principal: PortraitWeatherUI e LandscapeWeatherUI
    │   ├── WeatherBackground.kt         → Fundo gradiente dinâmico por condição meteorológica
    │   ├── WeatherParticles.kt          → Efeitos de partículas animadas (chuva, neve)
    │   ├── WeatherCard.kt               → Composable de cartão de informação meteorológica
    │   ├── WeatherRow.kt                → Linha compacta de métricas
    │   ├── MetricCard.kt                → Cartão individual de métrica com ícone Material
    │   ├── CoordinatesCard.kt           → Input de latitude/longitude com botão de mapa
    │   ├── FavoritesBar.kt              → Barra horizontal de localizações favoritas
    │   ├── LocationPickerActivity.kt    → Ecrã de seleção de localização por nome
    │   ├── WeatherUiState.kt            → Data class com todo o estado da UI
    │   └── theme/
    │       ├── Color.kt                 → Paleta de cores (gradientes, texto)
    │       ├── Theme.kt                 → JetpackWeatherAppTheme
    │       └── Type.kt                  → Tipografia
    └── viewmodel/
        └── WeatherViewModel.kt          → AndroidViewModel + StateFlow + FavoritesRepository
```

---

## 3. Implementação

### 3.1 Cliente HTTP com Ktor

A chamada à API Open-Meteo é feita com `Ktor` numa coroutine (`suspend fun`), sem necessidade de criar `Thread` manualmente. O cliente usa `ContentNegotiation` com `kotlinx.serialization` para desserializar o JSON diretamente para data classes:

```
https://api.open-meteo.com/v1/forecast?
  latitude={lat}&longitude={lon}
  &current_weather=true
  &hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m,
           apparent_temperature,relativehumidity_2m,visibility
  &daily=uv_index_max
  &timezone=auto
```

### 3.2 Arquitetura MVVM com StateFlow

O `WeatherViewModel` mantém dois `StateFlow`:

- `uiState: StateFlow<WeatherUiState>` — todo o estado meteorológico (temperatura, vento, pressão, sensação térmica, humidade, visibilidade, UV, dia/noite, loading)
- `favorites: StateFlow<List<FavoriteLocation>>` — lista de localizações favoritas

A UI observa com `collectAsState()` e recompõe automaticamente quando o estado muda.

### 3.3 Fundos Gradiente e Partículas Animadas

O `WeatherBackground` aplica um gradiente vertical com 5 paletas distintas:

| Condição | Paleta |
|---|---|
| Céu limpo (dia) | Azul céu claro |
| Céu limpo (noite) | Azul escuro / índigo |
| Chuva / nublado | Cinzento azulado |
| Neve | Branco azulado |
| Trovoada | Roxo escuro / cinzento |

O `WeatherParticles` sobrepõe animações de gotas de chuva ou flocos de neve conforme o weathercode.

### 3.4 Localizações Favoritas Persistidas

`FavoritesRepository` persiste a lista de favoritos em `SharedPreferences` como JSON (`kotlinx.serialization`). Cada `FavoriteLocation` tem nome, latitude e longitude. O ViewModel expõe as operações `addFavorite`, `deleteFavorite` e `onFavoriteTap` (que atualiza as coordenadas e faz novo pedido à API).

### 3.5 Layout Adaptativo (Portrait e Landscape)

O composable `WeatherUI` deteta a orientação via `LocalConfiguration` e serve:

- **Portrait** — coluna vertical com hero de temperatura, grelha de métricas, barra de favoritos e botão de atualização
- **Landscape** — layout de duas colunas: temperatura à esquerda, controlos e métricas à direita com scroll independente

---

## 4. Dependências

| Biblioteca | Versão | Finalidade |
|---|---|---|
| `androidx.compose:compose-bom` | 2026.02.01 | Jetpack Compose (BOM) |
| `io.ktor:ktor-client-android` | 2.3.12 | Cliente HTTP assíncrono |
| `io.ktor:ktor-client-content-negotiation` | 2.3.12 | Negociação de conteúdo JSON |
| `io.ktor:ktor-serialization-kotlinx-json` | 2.3.12 | Adaptador kotlinx.serialization para Ktor |
| `org.jetbrains.kotlinx:kotlinx-serialization-json` | 1.7.3 | Desserialização JSON |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.10.0 | ViewModel integrado em Compose |

---

## 5. Permissões

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
