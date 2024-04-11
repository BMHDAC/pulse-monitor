package pl.pw.mierzopuls.ui.components

import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import pl.pw.mierzopuls.R

@Composable
fun ArrowIndicator(isExpanded: Boolean) {
    Icon(
        painter = painterResource(
            if (isExpanded) R.drawable.ic_arrow_hide else R.drawable.ic_arrow_expand
        ),
        contentDescription = null
    )
}