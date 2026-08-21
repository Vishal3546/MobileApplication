package com.mobile.app.presentation.sales.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SaleListViewModel = hiltViewModel()
) {
    val items = viewModel.salesPagingFlow.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales") },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.updateSearch(it)
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                placeholder = { Text("Search sales...") }
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items.itemCount) { index ->
                    val sale = items[index]
                    if (sale != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            onClick = { onNavigateToDetail(sale.id.toString()) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Sale #${sale.saleNumber}", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Status: ${sale.saleStatus}")
                                Text(text = "Final Amount: $${sale.finalAmount}")
                            }
                        }
                    }
                }
            }
        }
    }
}
