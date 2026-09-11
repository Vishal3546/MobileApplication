package com.mobile.app.presentation.purchase

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.presentation.customer.CustomerActionState
import com.mobile.app.presentation.customer.CustomerFormContent
import com.mobile.app.presentation.customer.CustomerViewModel
import com.mobile.app.presentation.device.condition.DeviceConditionContent
import com.mobile.app.presentation.device.create.DeviceFormContent
import com.mobile.app.presentation.device.inspection.DeviceInspectionContent
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuybackWizardScreen(
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit,
    viewModel: PurchaseWizardViewModel = hiltViewModel(),
    customerViewModel: CustomerViewModel = hiltViewModel()
) {
    val wizardState by viewModel.wizardState.collectAsState()
    val customerActionState by customerViewModel.actionState.collectAsState()

    // Handle customer creation success in step 5
    LaunchedEffect(customerActionState) {
        if (customerActionState is CustomerActionState.Success) {
            val customerId = wizardState.customerId ?: ""
            viewModel.createFinalPurchase(customerId, "Buyback initiated via wizard")
            customerViewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Buyback", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (wizardState.currentStep > 1) viewModel.previousStep() else onNavigateBack()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LinearProgressIndicator(
                progress = wizardState.currentStep / 6f,
                modifier = Modifier.fillMaxWidth()
            )
            
            when (wizardState.currentStep) {
                1 -> {
                    DeviceFormContent(
                        buttonText = "Next: Functional Test",
                        isLoading = wizardState.isLoading,
                        isFetchingImei = wizardState.isFetchingImei,
                        fetchedDevice = wizardState.fetchedDeviceDetails,
                        onImeiEntered = { viewModel.fetchDeviceDetails(it) },
                        errorMessage = wizardState.error,
                        onSubmit = { viewModel.submitDeviceInfo(it) }
                    )
                }
                2 -> {
                    DeviceInspectionContent(
                        buttonText = "Next: Physical Condition",
                        isLoading = wizardState.isLoading,
                        errorMessage = wizardState.error,
                        onSubmit = { viewModel.submitInspection(it) }
                    )
                }
                3 -> {
                    DeviceConditionContent(
                        buttonText = "Next: Pricing",
                        isLoading = wizardState.isLoading,
                        errorMessage = wizardState.error,
                        onSubmit = { viewModel.submitCondition(it) }
                    )
                }
                4 -> {
                    ValuationStep(
                        onNext = { suggested, negotiated, final ->
                            viewModel.setPrices(suggested, negotiated, final)
                        }
                    )
                }
                5 -> {
                    CustomerFormContent(
                        buttonText = "Complete Purchase",
                        isLoading = customerActionState is CustomerActionState.Loading || wizardState.isLoading,
                        errorMessage = (customerActionState as? CustomerActionState.Error)?.message ?: wizardState.error,
                        onSubmit = { firstName, lastName, phone, email, address ->
                            customerViewModel.createCustomer(
                                CreateCustomerRequestDto(firstName, lastName, phone, null, email, address)
                            )
                        }
                    )
                }
                6 -> {
                    PurchaseCompletionStep(
                        purchase = wizardState.currentPurchase,
                        isLoading = wizardState.isLoading,
                        onComplete = {
                            viewModel.completePurchase()
                        }
                    )
                }
            }
        }
    }

    if (wizardState.isComplete) {
        LaunchedEffect(Unit) {
            onComplete()
        }
    }
}

@Composable
fun ValuationStep(onNext: (BigDecimal, BigDecimal, BigDecimal) -> Unit) {
    var suggested by remember { mutableStateOf("0") }
    var negotiated by remember { mutableStateOf("0") }
    var final by remember { mutableStateOf("0") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Price Quotation", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(value = suggested, onValueChange = { suggested = it }, label = { Text("Suggested Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = negotiated, onValueChange = { negotiated = it }, label = { Text("Negotiated Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = final, onValueChange = { final = it }, label = { Text("Final Price") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { 
                onNext(
                    suggested.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    negotiated.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                    final.toBigDecimalOrNull() ?: BigDecimal.ZERO
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next: Customer Details")
        }
    }
}

@Composable
fun PurchaseCompletionStep(
    purchase: com.mobile.app.domain.model.purchase.Purchase?,
    isLoading: Boolean,
    onComplete: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Summary", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        
        if (purchase != null) {
            Text("Purchase #: ${purchase.purchaseNumber}")
            Text("Amount: ₹${purchase.finalPrice}")
            Text("Status: ${purchase.status}")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Finish & Pay")
            }
        }
    }
}
