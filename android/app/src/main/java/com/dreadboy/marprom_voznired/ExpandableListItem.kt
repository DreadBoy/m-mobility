package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableListItem(
    text: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val expanded = rememberSaveable {
        mutableStateOf(false)
    }

    Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            text()
            IconButton({
                expanded.value = !expanded.value
            }, Modifier.height(24.dp).width(24.dp)) {
                Icon(
                    if (expanded.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    null
                )
            }
        }
        if (expanded.value)
            content.invoke()
    }

}