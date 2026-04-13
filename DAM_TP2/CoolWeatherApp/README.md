# CoolWeatherApp — Aplicação Android de Meteorologia

**Curso:** Licenciatura em Engenharia Informática e Multimédia  
**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Aluno:** Hugo Spencer Pereira de Sousa — `a46104`  
**Ano Letivo:** 2025/26  
**Fase:** Trabalho Prático 2 (TP2)

---

## 1. Overview

A **CoolWeatherApp** é uma aplicação Android que apresenta as condições meteorológicas atuais com base na localização real do dispositivo. Integra a API pública **Open-Meteo** para obter dados em tempo real e adapta a interface de forma dinâmica ao clima e à orientação do ecrã.

O utilizador pode também introduzir manualmente coordenadas GPS e atualizar a previsão para qualquer localização no mundo.

---

## 2. Arquitetura do Sistema

**Linguagem:** Kotlin  
**Min SDK:** API 24 (Android 7.0 Nougat)  
**Build System:** Gradle (Kotlin DSL)  
**Package:** `dam_A46104.coolweatherapp`  
**Layout Engine:** ConstraintLayout

```
CoolWeatherApp/
└── app/src/main/
    ├── java/dam_A46104/coolweatherapp/
    │   ├── MainActivity.kt          → Lógica principal, GPS, chamada à API e atualização da UI
    │   └── WeatherData.kt           → Data classes para deserialização do JSON (Open-Meteo)
    ├── res/
    │   ├── layout/
    │   │   └── activity_main.xml    → Layout portrait
    │   ├── layout-land/
    │   │   └── activity_main.xml    → Layout landscape (variante automática)
    │   ├── drawable/
    │   │   ├── fundo_dia.png        → Fundo portrait (dia)
    │   │   ├── fundo_noite.png      → Fundo portrait (noite)
    │   │   ├── fundo_dia_land.png   → Fundo landscape (dia)
    │   │   ├── fundo_noite_land.png → Fundo landscape (noite)
    │   │   └── *.xml                → Ícones vetoriais por condição meteorológica (28 estados)
    │   ├── values/
    │   │   ├── strings.xml          → Todos os textos externalizados
    │   │   ├── colors.xml           → Paleta de cores
    │   │   ├── themes.xml           → Temas dia/noite (portrait e landscape)
    │   │   └── arrays.xml           → Mapeamento weathercode → nome do drawable
    │   ├── values-night/
    │   │   └── themes.xml           → Override de tema para modo escuro do sistema
    │   └── values-pt-rPT/
    │       └── values-pt.xml        → Strings localizadas em português
    └── AndroidManifest.xml          → Permissões INTERNET, ACCESS_FINE/COARSE_LOCATION
```

---

## 3. Implementação

### 3.1 Localização GPS
A app usa o **`FusedLocationProviderClient`** (Google Play Services) para obter as coordenadas reais do dispositivo no arranque:

1. Ao iniciar, a app verifica se a permissão `ACCESS_FINE_LOCATION` foi concedida.
2. Se não houver permissão, é apresentado o pop-up nativo do Android ao utilizador.
3. Se o utilizador aceitar (`onRequestPermissionsResult`), a app obtém `lastLocation` e chama a API com as coordenadas reais.
4. Se o utilizador recusar ou o GPS estiver desligado, a app usa Lisboa (38.076°N, -9.12°W) como *fallback*.

### 3.2 Integração com a API Open-Meteo
A chamada HTTP é feita numa `Thread` separada para não bloquear a Main Thread. O URL é construído com `buildString` e a resposta JSON é desserializada com **Gson** para as data classes `WeatherData`, `CurrentWeather` e `Hourly`.

```
https://api.open-meteo.com/v1/forecast?
  latitude={lat}&longitude={lon}
  &current_weather=true
  &hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m
```

### 3.3 Ícones Dinâmicos por Condição Meteorológica
O mapeamento entre o `weathercode` da API e o drawable correspondente está externalizado em `arrays.xml` (sem código *hardcoded*):

- `weather_codes` — array de inteiros com os códigos WMO
- `weather_images` — array de strings com os nomes base dos drawables

Para os códigos 0 (céu limpo), 1 (maioritariamente limpo) e 2 (parcialmente nublado), é adicionado o sufixo `"day"` ou `"night"` ao nome base, conforme a variável `day`.

### 3.4 Temas e Fundos Dinâmicos
O tema é aplicado **antes** de `super.onCreate()`, como exigido pelo Android. São usados 4 temas distintos:

| Orientação | Período | Tema |
|---|---|---|
| Portrait | Dia | `Theme_Day` |
| Portrait | Noite | `Theme_Night` |
| Landscape | Dia | `Theme_Day_Land` |
| Landscape | Noite | `Theme_Night_Land` |

O fundo de ecrã correspondente é definido no atributo `android:windowBackground` de cada tema.

### 3.5 Atualização Manual
O utilizador pode introduzir manualmente latitude e longitude nos campos `EditText` e pressionar o botão **Atualizar** para obter a previsão de qualquer localização.

---

## 4. Dependências

| Biblioteca | Versão | Finalidade |
|---|---|---|
| `com.google.code.gson` | 2.8.9 | Desserialização do JSON da Open-Meteo |
| `com.google.android.gms:play-services-location` | 21.1.0 | Obtenção das coordenadas GPS |

---

## 5. Permissões

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```
