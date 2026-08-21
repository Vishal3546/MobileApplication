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

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val isEdit = customerId != null

    LaunchedEffect(customerId) {
        if (isEdit) {
            viewModel.loadCustomer(UUID.fromString(customerId))
        }
    }
    
    LaunchedEffect(detailState) {
        if (isEdit && detailState is CustomerDetailState.Success) {
            val customer = (detailState as CustomerDetailState.Success).customer
            firstName = customer.firstName
            lastName = customer.lastName
            phone = customer.phone
            email = customer.email ?: ""
            address = customer.address ?: ""
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
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
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

            if (actionState is CustomerActionState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (isEdit) {
                            viewModel.updateCustomer(
                                id = UUID.fromString(customerId),
                                request = UpdateCustomerRequestDto(
                                    firstName = firstName,
                                    lastName = lastName,
                                    phone = phone,
                                    altPhone = null,
                                    email = email.takeIf { it.isNotBlank() },
                                    address = address.takeIf { it.isNotBlank() },
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
                                    email = email.takeIf { it.isNotBlank() },
                                    address = address.takeIf { it.isNotBlank() }
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = firstName.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
                ) {
                    Text("Save")
                }
            }

            if (actionState is CustomerActionState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (actionState as CustomerActionState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
