package cl.reclama.app.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AdaptiveLayoutClass {
    COMPACT,
    REGULAR,
    WIDE
}

@Immutable
data class AdaptiveLayoutPolicy(
    val layoutClass: AdaptiveLayoutClass,
    val horizontalPadding: Dp,
    val maxContentWidth: Dp?
)

fun classifyAdaptiveLayout(widthDp: Int): AdaptiveLayoutClass = when {
    widthDp < 360 -> AdaptiveLayoutClass.COMPACT
    widthDp < 840 -> AdaptiveLayoutClass.REGULAR
    else -> AdaptiveLayoutClass.WIDE
}

@Composable
fun rememberAdaptiveLayoutPolicy(): AdaptiveLayoutPolicy {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return remember(widthDp) {
        when (val layoutClass = classifyAdaptiveLayout(widthDp)) {
            AdaptiveLayoutClass.COMPACT -> AdaptiveLayoutPolicy(layoutClass, 14.dp, null)
            AdaptiveLayoutClass.REGULAR -> AdaptiveLayoutPolicy(layoutClass, 20.dp, null)
            AdaptiveLayoutClass.WIDE -> AdaptiveLayoutPolicy(layoutClass, 24.dp, 1040.dp)
        }
    }
}

@Composable
fun AdaptiveStage(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(AdaptiveLayoutPolicy) -> Unit
) {
    val policy = rememberAdaptiveLayoutPolicy()
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.TopCenter
    ) {
        val stageModifier = if (policy.maxContentWidth != null) {
            Modifier.fillMaxSize().widthIn(max = policy.maxContentWidth)
        } else {
            Modifier.fillMaxSize()
        }
        Box(modifier = stageModifier) {
            content(policy)
        }
    }
}
