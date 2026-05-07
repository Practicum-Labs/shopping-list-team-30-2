package ru.ya.practicum.shopper.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.ya.practicum.shopper.R

private data class SearchTextFieldState(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val onClose: () -> Unit,
    val onSearch: () -> Unit,
    val focusRequester: FocusRequester,
    val onFocusChanged: (Boolean) -> Unit,
    val focusManager: FocusManager
)

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onSearch: () -> Unit,
    content: @Composable () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        SearchTextField(
            state = SearchTextFieldState(
                query = query,
                onQueryChange = onQueryChange,
                onClose = onClose,
                onSearch = onSearch,
                focusRequester = focusRequester,
                onFocusChanged = { isFocused = it },
                focusManager = focusManager
            )
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (query.isNotEmpty()) {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
        ) {
            content()
            if (isFocused && query.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
        }
    }
}

@Composable
private fun SearchTextField(state: SearchTextFieldState) {
    TextField(
        value = state.query,
        onValueChange = state.onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .focusRequester(state.focusRequester)
            .onFocusChanged { state.onFocusChanged(it.isFocused) },
        placeholder = {
            Text(
                text = stringResource(R.string.search_lists_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = { SearchLeadingIcon(state.focusManager, state.onClose) },
        trailingIcon = { SearchTrailingIcon(state.query, state.onQueryChange) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            state.focusManager.clearFocus()
            state.onSearch()
        })
    )
}

@Composable
private fun SearchLeadingIcon(focusManager: FocusManager, onClose: () -> Unit) {
    IconButton(onClick = {
        focusManager.clearFocus()
        onClose()
    }) {
        Icon(
            painter = painterResource(id = R.drawable.back),
            contentDescription = stringResource(R.string.cd_back),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SearchTrailingIcon(query: String, onQueryChange: (String) -> Unit) {
    if (query.isNotEmpty()) {
        IconButton(onClick = { onQueryChange("") }) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = stringResource(R.string.cd_clear),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
