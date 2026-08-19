package compose.project.leshy.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import compose.project.leshy.data.platform.PhotoStorage
import compose.project.leshy.domain.model.Category
import compose.project.leshy.domain.model.CategoryIconSource
import compose.project.leshy.domain.model.iconSource
import leshy.shared.generated.resources.Res
import leshy.shared.generated.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

/**
 * The one place that turns a [Category] into its picture — bundled catalog illustration
 * (`Res.allDrawableResources`) or the user's own file, transparently to the caller. Every screen
 * showing a species' image goes through this: the Record tile, the Filter dialog, the collection
 * picker, the walk-detail donut legend, and the marker-size preview in Settings. The map's markers
 * can't use it (MapLibre wants a `Painter`, not a composable) and go through
 * `rememberMushroomMarkerPainter` instead, which branches on the same [CategoryIconSource].
 *
 * A species with no illustration at all renders as empty space of the same size rather than
 * nothing, so a list row's layout doesn't shift depending on whether an image exists.
 */
@Composable
fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    when (val source = category.iconSource()) {
        null -> Spacer(modifier)

        is CategoryIconSource.Bundled -> {
            val drawable = Res.allDrawableResources[source.iconRef]
            if (drawable == null) {
                Spacer(modifier)
            } else {
                Image(
                    painter = painterResource(drawable),
                    contentDescription = contentDescription,
                    modifier = modifier,
                    contentScale = contentScale,
                )
            }
        }

        is CategoryIconSource.UserFile -> {
            // Same "file://" + Coil route as walk thumbnails and place photos (see WalkCard) —
            // Coil resolves local file URIs on both platforms with no extra decoder.
            val photoStorage = koinInject<PhotoStorage>()
            AsyncImage(
                model = "file://${photoStorage.resolvePath(source.fileName)}",
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
    }
}
