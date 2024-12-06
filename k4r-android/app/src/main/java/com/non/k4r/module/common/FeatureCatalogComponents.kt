package com.non.k4r.module.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.non.k4r.core.constant.SystemFeatures
import com.non.k4r.ui.theme.AppTheme

private const val TAG = "FeatureCatalogComponents"

@Composable
fun FeatureCatalogItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit,
    title: String
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        OutlinedCard(
            modifier
                .fillMaxSize()
                .padding(24.dp)
                .clickable(onClick = onClick)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    icon()
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        textAlign = TextAlign.Center,
                        text = title,
                    )
                }
            }
        }
    }

}

@Composable
fun FeatureCatalogScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    AppTheme {
        Surface(
            modifier = modifier
                .fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                contentPadding = PaddingValues(4.dp)
            ) {
                items(SystemFeatures.entries.toTypedArray()) { entry ->
                    FeatureCatalogItem(
                        onClick = {
                            when (entry) {
                                SystemFeatures.Expenditure -> navController.navigate(route = ExpenditureSubmitRoute)
                                SystemFeatures.Todo -> navController.navigate(route = TodoSubmitRoute)
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = entry.iconResId()),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                        },
                        title = entry.featureName()
                    )
                }
            }
        }
    }


}