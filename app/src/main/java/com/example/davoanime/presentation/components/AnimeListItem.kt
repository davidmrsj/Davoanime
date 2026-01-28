package com.example.davoanime.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.davoanime.R
import com.example.davoanime.domain.model.Reciente

@Composable
fun AnimeListItem(modifier: Modifier, anime: Reciente, onclick: (Reciente) -> Unit){

    val outerShape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_20))
    val innerShape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_20))

    Card(modifier = modifier.clickable { onclick(anime) },
        shape = outerShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_8))) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(anime.image)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Preview
@Composable
private fun FeaturedAnimeCardPreview() {
    val reciente: Reciente = Reciente(
        id = 1,
        number = 12,
        title = "Blue Lock",
        image = "https://cdn.jkdesu.com/assets/images/animes/image/jigoku-sensei-nube-2025-part-2.jpg",
        thumbnail = "https://cdn.example.com/bluelock_thumb.jpg",
        animeId = 100,
        timestamp = "2026-01-22T18:00:00Z"
    )
    AnimeListItem(
        modifier = Modifier.fillMaxWidth(),
        anime = reciente,
        onclick = TODO()
    )
}