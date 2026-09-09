package com.mobile.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.data.remote.dto.UpdateCustomerRequestDto
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    customerId: String?, // null if creating
    onNavigateBack: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val actionState by viewModel.actionState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    val isEdit = customerId != null

    LaunchedEffect(customerId) {
        if (isEdit) {
            viewModel.loadCustomer(UUID.fromString(customerId))
        }
    }

    LaunchedEffect(actionState) {
        if (actionState is CustomerActionState.Success) {
            viewModel.resetActionState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEdit) "Edit Customer" else "Create Customer") })
        }
    ) { padding ->
        val initialFirstName = (detailState as? CustomerDetailState.Success)?.customer?.firstName ?: ""
        val initialLastName = (detailState as? CustomerDetailState.Success)?.customer?.lastName ?: ""
        val initialPhone = (detailState as? CustomerDetailState.Success)?.customer?.phone ?: ""
        val initialEmail = (detailState as? CustomerDetailState.Success)?.customer?.email ?: ""
        val initialAddress = (detailState as? CustomerDetailState.Success)?.customer?.address ?: ""

        CustomerFormContent(
            modifier = Modifier.padding(padding),
            initialFirstName = initialFirstName,
            initialLastName = initialLastName,
            initialPhone = initialPhone,
            initialEmail = initialEmail,
            initialAddress = initialAddress,
            isLoading = actionState is CustomerActionState.Loading,
            errorMessage = (actionState as? CustomerActionState.Error)?.message,
            onSubmit = { firstName, lastName, phone, email, address ->
                if (isEdit) {
                    viewModel.updateCustomer(
                        id = UUID.fromString(customerId),
                        request = UpdateCustomerRequestDto(
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone,
                            altPhone = null,
                            email = email,
                            address = address,
                            status = null
                        )
                    )
                } else {
                    viewModel.createCustomer(
                        request = CreateCustomerRequestDto(
                            firstName = firstName,
                            lastName = lastName,
                            phone = phone,
                            altPhone = null,
                            email = email,
                            address = address
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun CustomerFormContent(
    modifier: Modifier = Modifier,
    initialFirstName: String = "",
    initialLastName: String = "",
    initialPhone: String = "",
    initialEmail: String = "",
    initialAddress: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    buttonText: String = "Save",
    onSubmit: (String, String, String, String?, String?) -> Unit
) {
    var firstName by remember(initialFirstName) { mutableStateOf(initialFirstName) }
    var lastName by remember(initialLastName) { mutableStateOf(initialLastName) }
    var phone by remember(initialPhone) { mutableStateOf(initialPhone) }
    var email by remember(initialEmail) { mutableStateOf(initialEmail) }
    var address by remember(initialAddress) { mutableStateOf(initialAddress) }

    Column(modifier = modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    onSubmit(firstName, lastName, phone, email.takeIf { it.isNotBlank() }, address.takeIf { it.isNotBlank() })
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
            ) {
                Text(buttonText)
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
