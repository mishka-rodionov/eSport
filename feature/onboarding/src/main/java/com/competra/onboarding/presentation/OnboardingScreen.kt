package com.competra.onboarding.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.competra.designsystem.components.DSButton
import com.competra.domain.models.onboarding.OnboardingSource
import com.competra.onboarding.data.OnboardingAction
import com.competra.resources.R
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private data class OnboardingSlide(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
)

private val onboardingSlides = listOf(
    OnboardingSlide(R.drawable.ic_check_24px, R.string.onboarding_slide_1_title, R.string.onboarding_slide_1_subtitle),
    OnboardingSlide(R.drawable.ic_build_24px, R.string.onboarding_slide_2_title, R.string.onboarding_slide_2_subtitle),
    OnboardingSlide(R.drawable.ic_groups_24px, R.string.onboarding_slide_3_title, R.string.onboarding_slide_3_subtitle),
    OnboardingSlide(R.drawable.ic_star_24px, R.string.onboarding_slide_4_title, R.string.onboarding_slide_4_subtitle),
)

/**
 * Полноэкранный онбординг. [viewModelKey] должен быть уникальным на каждый показ
 * (см. вызовы в AppRoot/MainScreen), чтобы не переиспользовать состояние прошлого показа.
 */
@Composable
fun OnboardingScreen(
    source: OnboardingSource,
    viewModelKey: String,
    onFinished: () -> Unit,
) {
    val viewModel: OnboardingViewModel =
        koinViewModel(key = viewModelKey) { parametersOf(source) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { onboardingSlides.size })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            viewModel.onAction(OnboardingAction.PageChanged(page))
        }
    }
    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onFinished()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(200f),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TextButton(
                onClick = { viewModel.onAction(OnboardingAction.SkipClicked) },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                Text(text = stringResource(R.string.onboarding_skip))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingSlideContent(onboardingSlides[page])
            }

            OnboardingDotsIndicator(
                pageCount = onboardingSlides.size,
                currentPage = state.currentPage,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            val isLastPage = state.currentPage == onboardingSlides.lastIndex
            DSButton(
                text = stringResource(if (isLastPage) R.string.onboarding_start else R.string.onboarding_next),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onClick = {
                    viewModel.onAction(
                        if (isLastPage) OnboardingAction.StartClicked else OnboardingAction.NextClicked
                    )
                }
            )
        }
    }
}

@Composable
private fun OnboardingSlideContent(slide: OnboardingSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(slide.icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(slide.title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(slide.subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun OnboardingDotsIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}
