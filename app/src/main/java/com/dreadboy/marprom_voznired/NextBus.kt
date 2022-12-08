package com.dreadboy.marprom_voznired

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun NextBus(times: List<String>) {
    var now by rememberSaveable { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            now = LocalDateTime.now()
        }
    }

    val nextBus = findNextTime(now, times)
    val duration = Duration.between(now, nextBus)

    Row {
        Text(
            LocalTime.MIN.plus(Duration.ofSeconds(duration.toMillis() / 1000))
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            Modifier
                .padding(vertical = 8.dp)
                .alignByBaseline(),
            style = MaterialTheme.typography.h5,
        )
        Text(
            "(Next bus ${nextBus.format(DateTimeFormatter.ofPattern("HH:mm"))})",
            Modifier
                .padding(start = 8.dp)
                .alignByBaseline(),
            style = MaterialTheme.typography.caption
        )
    }
}


fun findNextTime(now: LocalDateTime, times: List<String>): LocalDateTime {
    val parser = { time: String -> LocalTime.parse(time).atDate(now.toLocalDate()) }
    val localTimes = times.map(parser)
        .plus(times.map { parser(it).plusHours(24) })
    val nextTime = localTimes
        .firstOrNull { !Duration.between(now, it).isNegative }
    return nextTime ?: localTimes.first()
}