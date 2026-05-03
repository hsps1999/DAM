# 07 — API Usage

## API escolhida

**Dog CEO API** — https://dog.ceo/dog-api

Razões da escolha:
- Sem necessidade de chave de API
- Sem rate limits restritivos para uso académico
- Resposta JSON minimalista (fácil de mapear)
- Endpoints estáveis e bem documentados

## Endpoints utilizados

### 1. Imagem aleatória única

```
GET https://dog.ceo/api/breeds/image/random
```

**Resposta:**
```json
{
  "message": "https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg",
  "status": "success"
}
```

### 2. Múltiplas imagens aleatórias (usado para popular a lista)

```
GET https://dog.ceo/api/breeds/image/random/{count}
```

Onde `{count}` é o número de imagens pretendidas (máx. 50 por pedido).

**Exemplo:** `https://dog.ceo/api/breeds/image/random/20`

**Resposta:**
```json
{
  "message": [
    "https://images.dog.ceo/breeds/terrier-norwich/n02094258_1003.jpg",
    "https://images.dog.ceo/breeds/spaniel-cocker/n02102318_2156.jpg",
    "https://images.dog.ceo/breeds/pug/n02110958_15626.jpg"
  ],
  "status": "success"
}
```

### 3. Lista de raças (opcional, para metadados no ecrã de detalhes)

```
GET https://dog.ceo/api/breeds/list/all
```

## Mapeamento para o data model

A raça é extraída do URL da imagem com parsing simples:

```
https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg
                              ^^^^^^^^^^^^
                              raça (segmento após /breeds/)
```

Regra: dividir o URL por `/`, apanhar o segmento imediatamente a seguir a `breeds`. Se contiver `-`, a primeira parte é a raça principal e a segunda a subraça (ex.: `hound-afghan` → raça: `hound`, subraça: `afghan`).

## Códigos de estado

| Status   | Significado                          | Ação                              |
| -------- | ------------------------------------ | --------------------------------- |
| success  | Pedido bem-sucedido                  | Processar `message`               |
| error    | Falha (raça inexistente, etc.)       | Mostrar Snackbar e usar cache     |

## Modelo de resposta (Kotlin)

```kotlin
data class DogApiResponse(
    val message: List<String>,
    val status: String
)
```

Para o endpoint de imagem única, `message` é `String` em vez de `List<String>` — pode ser tratado com dois DTOs separados ou com um adapter Gson custom.
