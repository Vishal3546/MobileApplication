package com.mobile.app.presentation.purchase.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseListScreen(
    viewModel: PurchaseListViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val purchases = viewModel.purchases.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchases") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Add Purchase")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    count = purchases.itemCount,
                    key = purchases.itemKey { it.id },
                    contentType = purchases.itemContentType { "Purchase" }
                ) { index ->
                    val purchase = purchases[index]
                    if (purchase != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onNavigateToDetail(purchase.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("No: ${purchase.purchaseNumber}", style = MaterialTheme.typography.titleMedium)
                                Text("Status: ${purchase.status}")
                                Text("Price: ₹${purchase.finalPrice}")
                            }
                        }
                    }
                }
                
                purchases.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)) }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)) }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            item {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                                    Button(onClick = { retry() }) { Text("Retry") }
                                }
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            val e = loadState.append as LoadState.Error
                            item {
                                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Error: ${e.error.localizedMessage}", color = MaterialTheme.colorScheme.error)
                                    Button(onClick = { retry() }) { Text("Retry") }
                                }
                            }
                        }
                        loadState.refresh is LoadState.NotLoading && purchases.itemCount == 0 -> {
                            item { Text("No purchases found.", modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentWidth(Alignment.CenterHorizontally)) }
                        }
                    }
                }
            }
        }
    }
}
