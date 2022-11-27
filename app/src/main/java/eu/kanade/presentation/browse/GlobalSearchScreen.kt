package eu.kanade.presentation.browse

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import eu.kanade.domain.manga.model.Manga
import eu.kanade.presentation.browse.components.GlobalSearchCardRow
import eu.kanade.presentation.browse.components.GlobalSearchErrorResultItem
import eu.kanade.presentation.browse.components.GlobalSearchLoadingResultItem
import eu.kanade.presentation.browse.components.GlobalSearchResultItem
import eu.kanade.presentation.browse.components.GlobalSearchToolbar
import eu.kanade.presentation.components.LazyColumn
import eu.kanade.presentation.components.Scaffold
import eu.kanade.presentation.util.padding
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchItemResult
import eu.kanade.tachiyomi.ui.browse.source.globalsearch.GlobalSearchState
import eu.kanade.tachiyomi.util.system.LocaleHelper

@Composable
fun GlobalSearchScreen(
    state: GlobalSearchState,
    navigateUp: () -> Unit,
    onChangeSearchQuery: (String?) -> Unit,
    onSearch: (String) -> Unit,
    getManga: @Composable (CatalogueSource, Manga) -> State<Manga>,
    onClickSource: (CatalogueSource) -> Unit,
    onClickItem: (Manga) -> Unit,
    onLongClickItem: (Manga) -> Unit,
) {
    Scaffold(
        topBar = {
            GlobalSearchToolbar(
                searchQuery = state.searchQuery,
                progress = state.progress,
                total = state.total,
                navigateUp = navigateUp,
                onChangeSearchQuery = onChangeSearchQuery,
                onSearch = onSearch,
            )
        },
    ) { paddingValues ->
        GlobalSearchContent(
            items = state.items,
            contentPadding = paddingValues,
            getManga = getManga,
            onClickSource = onClickSource,
            onClickItem = onClickItem,
            onLongClickItem = onLongClickItem,
        )
    }
}

@Composable
fun GlobalSearchContent(
    items: Map<CatalogueSource, GlobalSearchItemResult>,
    contentPadding: PaddingValues,
    getManga: @Composable (CatalogueSource, Manga) -> State<Manga>,
    onClickSource: (CatalogueSource) -> Unit,
    onClickItem: (Manga) -> Unit,
    onLongClickItem: (Manga) -> Unit,
) {
    LazyColumn(
        contentPadding = contentPadding,
    ) {
        items.forEach { (source, result) ->
            item {
                GlobalSearchResultItem(
                    title = source.name,
                    subtitle = LocaleHelper.getDisplayName(source.lang),
                    onClick = { onClickSource(source) },
                ) {
                    when (result) {
                        is GlobalSearchItemResult.Error -> {
                            GlobalSearchErrorResultItem(message = result.throwable.message)
                        }
                        GlobalSearchItemResult.Loading -> {
                            GlobalSearchLoadingResultItem()
                        }
                        is GlobalSearchItemResult.Success -> {
                            if (result.isEmpty) {
                                Text(
                                    text = stringResource(id = R.string.no_results_found),
                                    modifier = Modifier
                                        .padding(
                                            horizontal = MaterialTheme.padding.medium,
                                            vertical = MaterialTheme.padding.small,
                                        ),
                                )
                                return@GlobalSearchResultItem
                            }

                            GlobalSearchCardRow(
                                titles = result.result,
                                getManga = { getManga(source, it) },
                                onClick = onClickItem,
                                onLongClick = onLongClickItem,
                            )
                        }
                    }
                }
            }
        }
    }
}
