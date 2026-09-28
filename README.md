# YT Music Clone (layout)

Reprodução visual da interface do YouTube Music para Android, feita com Kotlin e Jetpack Compose. O projeto cobre apenas o layout: não reproduz áudio, não acessa internet e não usa nenhum serviço do YouTube ou do Google.

Este é um projeto de estudo. Não tem relação com o Google nem com o YouTube, e não usa logos, imagens ou catálogo oficiais. O logo é desenhado por código como aproximação, e as músicas, artistas e capas são fictícios (capas são gradientes de cor).

## Telas implementadas

**Início**
- Barra superior com logo, botão de transmitir, busca e avatar
- Chips de humor (Energizar, Relaxar, Treinar, etc.)
- "Seleções rápidas": lista de músicas em páginas de 4 linhas, com rolagem horizontal
- Carrosséis: Ouvir de novo, Álbuns para você, Artistas similares (capas circulares) e Seus mixes

**Explorar**
- Três botões de atalho: Lançamentos, Paradas, Momentos e gêneros
- Carrossel de novos álbuns e singles
- Grade de gêneros com faixa colorida na lateral

**Biblioteca**
- Chips de filtro (Playlists, Músicas, Álbuns, Artistas, Podcasts)
- Linha de ordenação e botão de grade
- Playlists fixadas e lista de playlists

**Mini-player**
- Fica acima da navegação inferior, com capa, título, artista, play/pause e próxima
- Barra de progresso fina na parte de baixo
- Ao tocar nele, abre o player em tela cheia

**Player em tela cheia**
- Fundo em degradê baseado na cor da capa
- Alternância Música / Vídeo, botão de transmitir e menu
- Capa grande, título e artista
- Botões de curtir/não curtir, Salvar e Compartilhar
- Barra de progresso com tempos
- Controles: aleatório, anterior, play/pause, próxima e repetir
- Abas inferiores: A SEGUIR, LETRA e RELACIONADOS
- Voltar (gesto ou botão do sistema) fecha o player

## O que funciona e o que não funciona

Funciona:
- Navegação entre Início, Explorar e Biblioteca pela barra inferior
- Abrir e fechar o player em tela cheia
- Alternar play/pause (só troca o ícone)

Não funciona (é só visual):
- Reprodução de áudio, fila, curtidas, busca, downloads e playlists
- Barra de progresso (fica parada em 35%) e botões de anterior, próxima, aleatório e repetir
- Chips, botões "Mais" e menus de três pontos

## Tecnologias

| Item | Valor |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose com Material 3 |
| Ícones | `material-icons-extended` |
| minSdk | 24 |
| targetSdk | 36 |
| compileSdk | 37 |
| Namespace / applicationId | `com.example.ytmusicclone` |

Não há dependências externas além das bibliotecas do AndroidX e do Compose. Não é usada biblioteca de imagens nem de rede.

## Estrutura

Todo o código de interface está em um único arquivo:

```
app/src/main/java/com/example/ytmusicclone/MainActivity.kt
```

Organização interna do arquivo, de cima para baixo:

| Bloco | Conteúdo |
|---|---|
| Cores | Constantes de cor (`BgBlack`, `NavGray`, `TextGray`, `ChipBg`, `YtRed`) |
| Dados fictícios | Classe `Song`, lista `songs` e paletas de gradiente |
| `Cover` | Capa gerada por gradiente a partir de um número (`seed`) |
| `MainActivity` / `MusicApp` | Ponto de entrada e estado global (aba atual, player aberto, tocando) |
| `YtLogo` / `TopBar` | Logo desenhado em `Canvas` e barra superior |
| `BottomNav` / `MiniPlayer` | Navegação inferior e mini-player |
| Componentes comuns | `Chips`, `SectionHeader`, `SongRow`, `SquareCard`, `CardsRow` |
| `HomeScreen` | Tela Início |
| `ExploreScreen` | Tela Explorar |
| `LibraryScreen` / `LibRow` | Tela Biblioteca |
| `PlayerScreen` | Player em tela cheia |

## Como rodar

Requisitos:
- Android Studio recente (que tenha a plataforma Android 17 / API 37 disponível)
- JDK 11 ou superior (o Android Studio já inclui um)
- Emulador ou celular com Android 7.0 (API 24) ou mais novo

Passos:
1. Crie um projeto novo no Android Studio com o template **Empty Activity** (Compose), usando o pacote `com.example.ytmusicclone`.
2. Substitua o conteúdo de `MainActivity.kt` pelo código deste projeto.
3. Configure o `build.gradle.kts` do módulo `app` conforme a seção abaixo.
4. Clique em **Sync Now** e rode com o botão de play.

## Configuração do Gradle

No `build.gradle.kts` do módulo `app`:

Adicione a dependência dos ícones estendidos, sem número de versão (o BOM do Compose define a versão):

```kotlin
dependencies {
    // ...
    implementation("androidx.compose.material:material-icons-extended")
}
```

Suba o `compileSdk` para 37. As versões atuais de `androidx.core` e `androidx.lifecycle` exigem isso:

```kotlin
android {
    namespace = "com.example.ytmusicclone"
    compileSdk {
        version = release(37)
    }
    // ...
}
```

Mantenha `targetSdk = 36`. Só o `compileSdk` precisa mudar.

## Problemas comuns

**`checkDebugAarMetadata FAILED` pedindo compileSdk 37**
O projeto está compilando com uma versão menor do que a exigida pelas bibliotecas. Ajuste o `compileSdk` como descrito acima e, se necessário, instale a plataforma em **Tools → SDK Manager → SDK Platforms → Android 17 (API 37)**.

**Ícones em vermelho (`Unresolved reference`)**
Falta a dependência `material-icons-extended` ou o projeto não foi sincronizado. Adicione a linha e clique em **Sync Now**.

**`Icons.Filled.PushPin` não encontrado**
Em versões mais antigas da biblioteca de ícones esse ícone não existe. Troque por outro ícone disponível na função `LibRow`.

**Aviso `Icons.Filled.TrendingUp is deprecated`**
É só um aviso e não impede o build. Para removê-lo, use `Icons.AutoMirrored.Filled.TrendingUp` na função `ExploreScreen`.

**A sintaxe `release(37)` dá erro**
A versão do Android Gradle Plugin é mais antiga. Use `compileSdk = 37` no lugar do bloco.

## Renomear o projeto

São três nomes independentes:

- **Nome no celular:** edite `app_name` em `app/src/main/res/values/strings.xml`.
- **Nome na IDE:** edite `rootProject.name` em `settings.gradle.kts` e sincronize.
- **Pasta no disco:** feche o projeto, renomeie a pasta pelo Explorador de Arquivos e abra de novo com **File → Open**.

O pacote `com.example.ytmusicclone` não precisa ser alterado para mudar nenhum nome visível.

## Personalização

- **Cores:** ajuste as constantes no topo do arquivo. Os degradês das capas ficam na lista `palettes`.
- **Conteúdo:** edite a lista `songs` e os textos dentro de `HomeScreen`, `ExploreScreen` e `LibraryScreen`.
- **Capas reais:** substitua a função `Cover` por um componente de imagem (por exemplo, com a biblioteca Coil) e passe uma URL ou um recurso local em vez do `seed`.
- **Tema claro:** o layout foi feito só para o tema escuro. Para suportar tema claro, as cores fixas precisam ser movidas para o `MaterialTheme`.

## Próximos passos possíveis

1. Separar o arquivo único em pacotes (`ui/home`, `ui/explore`, `ui/library`, `ui/player`, `ui/components`).
2. Substituir os dados fictícios por um modelo de dados e um `ViewModel`.
3. Trocar a navegação por estado (`when (tab)`) por Navigation Compose.
4. Adicionar reprodução de áudio com Media3 (ExoPlayer) e `MediaSessionService`.
5. Carregar capas com Coil.
