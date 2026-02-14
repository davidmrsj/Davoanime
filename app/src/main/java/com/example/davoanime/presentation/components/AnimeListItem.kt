package com.example.davoanime.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.davoanime.R
import com.example.davoanime.domain.model.Reciente

@Composable
fun AnimeListItem(modifier: Modifier, anime: Reciente, onclick: (Reciente) -> Unit) {
    val imageShape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_12))

    Card(
        modifier = modifier.clickable { onclick(anime) },
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.radius_16)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.elevation_2))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(anime.image)
                    .crossfade(true)
                    .build(),
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(imageShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "EP ${anime.number}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun FeaturedAnimeCardPreview() {
    val reciente = Reciente(
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
        onclick = {}
    )
}