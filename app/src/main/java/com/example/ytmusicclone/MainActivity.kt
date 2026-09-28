package com.example.ytmusicclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Cores ----------
private val BgBlack = Color(0xFF000000)
private val NavGray = Color(0xFF212121)
private val TextGray = Color(0xFFAAAAAA)
private val ChipBg = Color(0x1AFFFFFF)
private val YtRed = Color(0xFFFF0000)

// ---------- Dados fictícios ----------
data class Song(val title: String, val artist: String, val seed: Int)

private val songs = listOf(
    Song("Noite de Verão", "Lua Cheia", 0), Song("Maré Alta", "Rio Azul", 1),
    Song("Fim de Tarde", "Coral Vivo", 2), Song("Estrada Longa", "Os Vagalumes", 3),
    Song("Cidade Dormindo", "Aurora Sul", 4), Song("Casa de Vidro", "Nuvem Rara", 5),
    Song("Sol da Manhã", "Trio Cobre", 6), Song("Pele de Chuva", "Mar & Vento", 7),
)

private val palettes = listOf(
    0xFFE65C00 to 0xFF7A1F00, 0xFF1D976C to 0xFF093028, 0xFF8E2DE2 to 0xFF2B0A5C,
    0xFF2193B0 to 0xFF0B2E4A, 0xFFDD2476 to 0xFF4A0D2B, 0xFFF7971E to 0xFF6B3A00,
    0xFF396AFC to 0xFF0D1B4D, 0xFF56AB2F to 0xFF16350B,
)

@Composable
fun Cover(seed: Int, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(4.dp)) {
    val (a, b) = palettes[seed % palettes.size]
    Box(modifier.clip(shape).background(Brush.linearGradient(listOf(Color(a), Color(b)))))
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(background = BgBlack, surface = BgBlack)) {
                MusicApp()
            }
        }
    }
}

// ---------- Raiz ----------
@Composable
fun MusicApp() {
    var tab by remember { mutableIntStateOf(0) }
    var playerOpen by remember { mutableStateOf(false) }
    var playing by remember { mutableStateOf(true) }
    BackHandler(playerOpen) { playerOpen = false }

    Box(Modifier.fillMaxSize().background(BgBlack)) {
        Scaffold(
            containerColor = BgBlack,
            topBar = { TopBar(listOf(null, "Explorar", "Biblioteca")[tab]) },
            bottomBar = {
                Column {
                    MiniPlayer(songs[0], playing, { playing = !playing }) { playerOpen = true }
                    BottomNav(tab) { tab = it }
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad)) {
                when (tab) {
                    0 -> HomeScreen()
                    1 -> ExploreScreen()
                    else -> LibraryScreen()
                }
            }
        }
        AnimatedVisibility(playerOpen, enter = slideInVertically { it }, exit = slideOutVertically { it }) {
            PlayerScreen(songs[0], playing, { playing = !playing }) { playerOpen = false }
        }
    }
}

// ---------- Barra superior ----------
@Composable
fun YtLogo(size: Dp = 28.dp) {
    Canvas(Modifier.size(size)) {
        val r = this.size.minDimension / 2
        val c = center
        drawCircle(YtRed, r)
        drawCircle(Color.White, r * 0.66f, style = Stroke(r * 0.1f))
        val p = Path().apply {
            moveTo(c.x - r * 0.2f, c.y - r * 0.3f)
            lineTo(c.x + r * 0.36f, c.y)
            lineTo(c.x - r * 0.2f, c.y + r * 0.3f)
            close()
        }
        drawPath(p, Color.White)
    }
}

@Composable
fun TopBar(title: String?) {
    Row(
        Modifier.fillMaxWidth().background(BgBlack).statusBarsPadding()
            .height(56.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title == null) {
            YtLogo()
            Spacer(Modifier.width(6.dp))
            Text("Music", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
        } else {
            Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.weight(1f))
        Icon(Icons.Outlined.Cast, "Transmitir", tint = Color.White, modifier = Modifier.padding(horizontal = 10.dp).size(24.dp))
        Icon(Icons.Filled.Search, "Pesquisar", tint = Color.White, modifier = Modifier.padding(horizontal = 10.dp).size(26.dp))
        Box(
            Modifier.padding(start = 10.dp).size(28.dp).clip(CircleShape).background(Color(0xFF6D4C41)),
            contentAlignment = Alignment.Center
        ) { Text("A", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
    }
}

// ---------- Navegação inferior e mini-player ----------
@Composable
fun BottomNav(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Triple("Início", Icons.Filled.Home, Icons.Outlined.Home),
        Triple("Explorar", Icons.Filled.Explore, Icons.Outlined.Explore),
        Triple("Biblioteca", Icons.Filled.LibraryMusic, Icons.Outlined.LibraryMusic),
    )
    NavigationBar(containerColor = NavGray, tonalElevation = 0.dp) {
        items.forEachIndexed { i, (label, filled, outlined) ->
            NavigationBarItem(
                selected = selected == i, onClick = { onSelect(i) },
                icon = { Icon(if (selected == i) filled else outlined, label) },
                label = { Text(label, fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, selectedTextColor = Color.White,
                    unselectedIconColor = TextGray, unselectedTextColor = TextGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun MiniPlayer(song: Song, playing: Boolean, onToggle: () -> Unit, onOpen: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color(0xFF2B2B2B)).clickable(onClick = onOpen)) {
        Row(Modifier.padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Cover(song.seed, Modifier.size(44.dp))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(song.title, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.artist, color = TextGray, fontSize = 13.sp, maxLines = 1)
            }
            IconButton(onClick = onToggle) {
                Icon(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = {}) { Icon(Icons.Filled.SkipNext, null, tint = Color.White, modifier = Modifier.size(30.dp)) }
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(Color(0x33FFFFFF))) {
            Box(Modifier.fillMaxWidth(0.35f).fillMaxHeight().background(Color.White))
        }
    }
}

// ---------- Componentes comuns ----------
@Composable
fun Chips(labels: List<String>, selected: Int? = null) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(labels.size) { i ->
            val sel = selected == i
            Box(
                Modifier.clip(RoundedCornerShape(8.dp))
                    .background(if (sel) Color.White else ChipBg)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) { Text(labels[i], color = if (sel) Color.Black else Color.White, fontSize = 14.sp) }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null, avatarSeed: Int? = null, more: Boolean = true) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        if (avatarSeed != null) {
            Cover(avatarSeed, Modifier.size(44.dp), CircleShape)
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            if (subtitle != null) Text(subtitle, color = TextGray, fontSize = 13.sp)
            Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        if (more) {
            Box(
                Modifier.clip(RoundedCornerShape(18.dp)).border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) { Text("Mais", color = Color.White, fontSize = 13.sp) }
        }
    }
}

@Composable
fun SongRow(song: Song) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Cover(song.seed, Modifier.size(52.dp))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(song.title, color = Color.White, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, color = TextGray, fontSize = 14.sp, maxLines = 1)
        }
        Icon(Icons.Filled.MoreVert, null, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun SquareCard(title: String, subtitle: String, seed: Int, circle: Boolean = false, size: Dp = 148.dp) {
    Column(Modifier.width(size)) {
        Cover(seed, Modifier.size(size), if (circle) CircleShape else RoundedCornerShape(4.dp))
        Text(title, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp), textAlign = if (circle) androidx.compose.ui.text.style.TextAlign.Center else null,
            fontWeight = FontWeight.Medium)
        Text(subtitle, color = TextGray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun CardsRow(items: List<Triple<String, String, Int>>, circle: Boolean = false) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { (t, s, seed) -> SquareCard(t, s, seed, circle) }
    }
}

// ---------- Início ----------
@Composable
fun HomeScreen() {
    LazyColumn(Modifier.fillMaxSize().background(BgBlack), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Spacer(Modifier.height(4.dp))
            Chips(listOf("Energizar", "Relaxar", "Treinar", "Deslocamento", "Festa", "Foco", "Dormir"))
        }
        item {
            SectionHeader("Seleções rápidas", "COMECE UMA RÁDIO A PARTIR DE UMA MÚSICA".lowercase().replaceFirstChar { it.uppercase() }, avatarSeed = 2, more = false)
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(2) { page ->
                    Column(Modifier.fillParentMaxWidth(0.9f)) { songs.drop(page * 4).take(4).forEach { SongRow(it) } }
                }
            }
        }
        item {
            SectionHeader("Ouvir de novo", "ALEX".lowercase().replaceFirstChar { it.uppercase() }, avatarSeed = 5)
            CardsRow(songs.map { Triple(it.title, it.artist, it.seed) })
        }
        item {
            SectionHeader("Álbuns para você")
            CardsRow(songs.reversed().map { Triple("Álbum ${it.title}", "Álbum • ${it.artist}", it.seed + 3) })
        }
        item {
            SectionHeader("Artistas similares", "Baseado em Lua Cheia")
            CardsRow(songs.map { Triple(it.artist, "12 mi de ouvintes", it.seed + 1) }, circle = true)
        }
        item {
            SectionHeader("Seus mixes")
            CardsRow(listOf(
                Triple("Meu Supermix", "Suas músicas favoritas e mais", 4),
                Triple("Mix 1", "Lua Cheia, Rio Azul e mais", 0),
                Triple("Mix 2", "Coral Vivo, Aurora Sul e mais", 6),
                Triple("Mix 3", "Trio Cobre, Mar & Vento e mais", 1),
            ))
        }
    }
}

// ---------- Explorar ----------
@Composable
fun ExploreScreen() {
    val genres = listOf("Sertanejo", "Pop", "Funk", "MPB", "Rock", "Pagode", "Rap e hip-hop", "Eletrônica", "Gospel", "Romântico")
    val barColors = palettes.map { Color(it.first) }
    LazyColumn(Modifier.fillMaxSize().background(BgBlack), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Lançamentos" to Icons.Filled.MusicNote, "Paradas" to Icons.Filled.TrendingUp, "Momentos e gêneros" to Icons.Filled.EmojiEmotions)
                    .forEach { (label, icon) ->
                        Row(
                            Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(NavGray).padding(horizontal = 10.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(label, color = Color.White, fontSize = 13.sp, maxLines = 2, lineHeight = 15.sp)
                        }
                    }
            }
        }
        item {
            SectionHeader("Novos álbuns e singles")
            CardsRow(songs.map { Triple(it.title, "Single • ${it.artist}", it.seed + 2) })
        }
        item {
            SectionHeader("Momentos e gêneros", more = false)
        }
        items(genres.chunked(2)) { pair ->
            Row(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { g ->
                    Row(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(6.dp)).background(NavGray)) {
                        Box(Modifier.width(6.dp).fillMaxHeight().background(barColors[genres.indexOf(g) % barColors.size]))
                        Text(g, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp))
                    }
                }
            }
        }
    }
}

// ---------- Biblioteca ----------
@Composable
fun LibraryScreen() {
    LazyColumn(Modifier.fillMaxSize().background(BgBlack), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Spacer(Modifier.height(4.dp))
            Chips(listOf("Playlists", "Músicas", "Álbuns", "Artistas", "Podcasts"), selected = 0)
        }
        item {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SwapVert, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(" Adicionado recentemente", color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Icon(Icons.Filled.GridView, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
        item {
            LibRow("Episódios para ouvir mais tarde", "Playlist automática", 6, pinned = true)
            LibRow("Músicas que eu gostei", "Playlist automática • 248 músicas", 4, pinned = true)
            LibRow("Baixadas", "Playlist automática • 12 músicas", 1, pinned = true)
        }
        items(songs) { LibRow("Minha playlist ${it.seed + 1}", "Playlist • Alex • ${it.seed + 8} músicas", it.seed) }
    }
}

@Composable
fun LibRow(title: String, subtitle: String, seed: Int, pinned: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Cover(seed, Modifier.size(56.dp))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(title, color = Color.White, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (pinned) Icon(Icons.Filled.PushPin, null, tint = TextGray, modifier = Modifier.size(14.dp))
                Text(subtitle, color = TextGray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Icon(Icons.Filled.MoreVert, null, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

// ---------- Player em tela cheia ----------
@Composable
fun PlayerScreen(song: Song, playing: Boolean, onToggle: () -> Unit, onClose: () -> Unit) {
    val top = Color(palettes[song.seed % palettes.size].second)
    Column(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(top, Color(0xFF0A0A0A), BgBlack)))
            .statusBarsPadding().navigationBarsPadding()
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp).height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(32.dp)) }
            Spacer(Modifier.weight(1f))
            Row(Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0x33FFFFFF)).padding(3.dp)) {
                Text("Música", color = Color.White, fontSize = 14.sp,
                    modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(Color(0x55000000)).padding(horizontal = 18.dp, vertical = 6.dp))
                Text("Vídeo", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp))
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) { Icon(Icons.Outlined.Cast, null, tint = Color.White) }
            IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null, tint = Color.White) }
        }

        Cover(song.seed, Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth().aspectRatio(1f)
            .shadow(16.dp, RoundedCornerShape(8.dp)), RoundedCornerShape(8.dp))

        Column(Modifier.padding(horizontal = 24.dp)) {
            Text(song.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(song.artist, color = TextGray, fontSize = 16.sp)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0x22FFFFFF)).padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Icon(Icons.Outlined.ThumbUp, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(18.dp))
                    Icon(Icons.Outlined.ThumbDown, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                listOf("Salvar" to Icons.Outlined.LibraryAdd, "Compartilhar" to Icons.Outlined.Share).forEach { (l, ic) ->
                    Row(Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0x22FFFFFF)).padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(ic, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(l, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Column(Modifier.padding(horizontal = 12.dp)) {
            Slider(
                value = 0.35f, onValueChange = {},
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color(0x4DFFFFFF))
            )
            Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                Text("1:12", color = TextGray, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("3:26", color = TextGray, fontSize = 12.sp)
            }
        }

        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Filled.Shuffle, null, tint = Color.White, modifier = Modifier.size(26.dp)) }
            IconButton(onClick = {}) { Icon(Icons.Filled.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
            Box(Modifier.size(72.dp).clip(CircleShape).background(Color.White).clickable(onClick = onToggle), contentAlignment = Alignment.Center) {
                Icon(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(42.dp))
            }
            IconButton(onClick = {}) { Icon(Icons.Filled.SkipNext, null, tint = Color.White, modifier = Modifier.size(40.dp)) }
            IconButton(onClick = {}) { Icon(Icons.Filled.Repeat, null, tint = Color.White, modifier = Modifier.size(26.dp)) }
        }

        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("A SEGUIR", "LETRA", "RELACIONADOS").forEach {
                Text(it, color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.8.sp)
            }
        }
    }
}