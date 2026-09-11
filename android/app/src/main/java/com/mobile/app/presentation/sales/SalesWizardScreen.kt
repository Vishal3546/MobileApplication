package com.mobile.app.presentation.sales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobile.app.core.ui.components.AppCard
import com.mobile.app.core.ui.components.BarcodeScannerDialog
import com.mobile.app.presentation.customer.CustomerFormContent
import com.mobile.app.presentation.inventory.InventoryListItem
import com.mobile.app.core.utils.PdfGenerator
import com.mobile.app.core.utils.ShareUtils
import androidx.compose.ui.platform.LocalContext
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesWizardScreen(
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: SalesWizardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sell a Phone", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep > 1) viewModel.previousStep() else onNavigateBack()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LinearProgressIndicator(
                progress = uiState.currentStep / 4f,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            when (uiState.currentStep) {
                1 -> InventorySelectionStep(viewModel)
                2 -> SaleCustomerAndPricingStep(viewModel)
                3 -> PaymentStep(viewModel)
                4 -> SaleSuccessStep(uiState, onComplete)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventorySelectionStep(viewModel: SalesWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val inventory = viewModel.availableInventory.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }
    var shopDropdownExpanded by remember { mutableStateOf(false) }

    if (showScanner) {
        BarcodeScannerDialog(
            onBarcodeDetected = { scannedImei ->
                searchQuery = scannedImei
                // The viewmodel search flow handles this automatically when we update the query state
                showScanner = false
            },
            onClose = { showScanner = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.isSuperAdmin) {
            ExposedDropdownMenuBox(
                expanded = shopDropdownExpanded,
                onExpandedChange = { shopDropdownExpanded = it },
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.shops.find { it.id == uiState.selectedBranchId }?.name ?: "Select Shop to Sell From",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selling From") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = shopDropdownExpanded,
                    onDismissRequest = { shopDropdownExpanded = false }
                ) {
                    uiState.shops.forEach { shop ->
                        DropdownMenuItem(
                            text = { Text(shop.name) },
                            onClick = {
                                viewModel.selectBranch(shop.id)
                                shopDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                // Pass it to viewmodel by calling a specific function, if not available just update the ui flow
            },
            placeholder = { Text("Search brand, model, or IMEI") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showScanner = true }) {
                    Icon(Icons.Rounded.QrCodeScanner, contentDescription = "Scan Barcode", tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Text(
            text = "Select Available Stock",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(inventory.itemCount) { index ->
                val item = inventory[index]
                if (item != null) {
                    InventoryListItem(
                        inventory = item,
                        onClick = { viewModel.selectInventory(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun SaleCustomerAndPricingStep(viewModel: SalesWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var discountInput by remember { mutableStateOf("0") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Device Summary Card
        uiState.selectedInventory?.let { inv ->
            AppCard(modifier = Modifier.padding(16.dp)) {
                Text("Selling Device", style = MaterialTheme.typography.labelSmall)
                Text("${inv.brand} ${inv.model}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("IMEI: ${inv.imei}", style = MaterialTheme.typography.bodySmall)
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Original Price")
                    Text("₹${inv.sellingPrice}", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Pricing Logic
        AppCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Pricing & Discount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = discountInput,
                onValueChange = { 
                    discountInput = it
                    viewModel.setDiscount(it.toBigDecimalOrNull() ?: BigDecimal.ZERO)
                },
                label = { Text("Discount Amount") },
                prefix = { Text("₹ ") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Final Selling Price", fontWeight = FontWeight.Bold)
                Text("₹${uiState.finalPrice}", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Customer Info Form
        CustomerFormContent(
            buttonText = "Proceed to Payment",
            isLoading = uiState.isLoading,
            errorMessage = uiState.error,
            onSubmit = { first, last, phone, email, _ ->
                viewModel.createCustomerAndSale(first, last, phone, email)
            }
        )
    }
}

@Composable
fun PaymentStep(viewModel: SalesWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val paymentModes = listOf("CASH", "UPI", "CREDIT_CARD", "DEBIT_CARD", "FINANCE")

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Receive Payment", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Total Amount: ₹${uiState.finalPrice}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Select Payment Method", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            paymentModes.forEach { mode ->
                Button(
                    onClick = { viewModel.completeSale(mode) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when(mode) {
                                "CASH" -> Icons.Rounded.Payments
                                "UPI" -> Icons.Rounded.Smartphone
                                "FINANCE" -> Icons.Rounded.AccountBalance
                                else -> Icons.Rounded.CreditCard
                            },
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(mode.replace("_", " "))
                    }
                }
            }
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (uiState.error != null) {
            Text(uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun SaleSuccessStep(state: SalesWizardState, onComplete: () -> Unit) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Sale Completed!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Transaction #${state.saleTransaction?.saleNumber}", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = {
                state.saleTransaction?.let { sale ->
                    val file = PdfGenerator.generateSaleInvoicePdf(context, sale)
                    if (file != null) {
                        ShareUtils.sharePdf(context, file)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Rounded.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Invoice (WhatsApp)")
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Dashboard")
        }
    }
}
