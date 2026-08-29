package com.mobile.app.core.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile.app.domain.model.AuthState
import com.mobile.app.presentation.auth.AuthViewModel
import com.mobile.app.presentation.auth.LoginScreen
import com.mobile.app.presentation.dashboard.DashboardScreen
import com.mobile.app.presentation.customer.CustomerListScreen
import com.mobile.app.presentation.customer.CustomerDetailScreen
import com.mobile.app.presentation.customer.CustomerFormScreen
import com.mobile.app.presentation.kyc.KycDocumentListScreen
import com.mobile.app.presentation.kyc.KycUploadScreen
import com.mobile.app.presentation.kyc.KycDocumentDetailScreen
import com.mobile.app.presentation.consent.ConsentScreen
import com.mobile.app.presentation.signature.SignatureScreen
import com.mobile.app.presentation.verificationvideo.VerificationVideoScreen
import com.mobile.app.presentation.device.list.DeviceListScreen
import com.mobile.app.presentation.device.create.CreateDeviceScreen
import com.mobile.app.presentation.device.detail.DeviceDetailScreen
import com.mobile.app.presentation.device.condition.DeviceConditionScreen
import com.mobile.app.presentation.device.condition.DeviceConditionHistoryScreen
import com.mobile.app.presentation.device.inspection.DeviceInspectionScreen
import com.mobile.app.presentation.device.inspection.DeviceInspectionHistoryScreen
import com.mobile.app.presentation.device.media.DeviceMediaScreen
import com.mobile.app.presentation.device.verification.ImeiVerificationScreen
import com.mobile.app.presentation.device.lifecycle.DeviceLifecycleScreen
import com.mobile.app.presentation.purchase.list.PurchaseListScreen
import com.mobile.app.presentation.purchase.create.CreatePurchaseScreen
import com.mobile.app.presentation.purchase.detail.PurchaseDetailScreen
import com.mobile.app.presentation.purchase.steps.PurchasePaymentScreen
import com.mobile.app.core.security.PermissionManager
import com.mobile.app.presentation.sales.list.SaleListScreen
import com.mobile.app.presentation.inventory.list.InventoryListScreen
import com.mobile.app.presentation.settlement.SettlementListScreen
import com.mobile.app.presentation.network_inventory.NetworkInventoryScreen
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    if (authState == AuthState.Loading) {
        // Just empty or a splash
        return
    }

    val startDestination = if (authState == AuthState.Authenticated) "dashboard" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            // Protected route check
            if (authState != AuthState.Authenticated) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // Clear backstack completely
                    }
                }
                return@composable
            }

            DashboardScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // clear back stack
                    }
                },
                onNavigateToSales = { navController.navigate("saleList") },
                onNavigateToPurchases = { navController.navigate("purchaseList") },
                onNavigateToInventory = { navController.navigate("inventoryList") },
                onNavigateToCustomers = { navController.navigate("customerList") },
                onNavigateToDevices = { navController.navigate("deviceList") },
                onNavigateToNetworkInventory = { navController.navigate("networkInventory") },
                onNavigateToSettlements = { navController.navigate("settlementList") }
            )
        }

        composable("customerList") {
            CustomerListScreen(
                onNavigateToDetail = { id -> navController.navigate("customerDetail/$id") },
                onNavigateToCreate = { navController.navigate("customerCreate") }
            )
        }

        composable("customerCreate") {
            CustomerFormScreen(
                customerId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("customerEdit/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            CustomerFormScreen(
                customerId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("customerDetail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            CustomerDetailScreen(
                customerId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editId -> navController.navigate("customerEdit/$editId") },
                onNavigateToKyc = { kycId -> navController.navigate("kycList/$kycId") },
                onNavigateToConsent = { consentId -> navController.navigate("consent/$consentId") },
                onNavigateToDevices = { customerIdParam -> navController.navigate("deviceList") } // Simulated navigation
            )
        }

        composable("kycList/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
            KycDocumentListScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUpload = { id -> navController.navigate("kycUpload/$id") },
                onNavigateToDetail = { docId -> navController.navigate("kycDetail/$customerId/$docId") }
            )
        }

        composable("kycUpload/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
            KycUploadScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("kycDetail/{customerId}/{documentId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            KycDocumentDetailScreen(
                customerId = customerId,
                documentId = documentId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("consent/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
            ConsentScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSignature = { id -> navController.navigate("signature/$id") }
            )
        }

        composable("signature/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
            SignatureScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() },
                onSignatureCaptured = { mediaId ->
                    navController.popBackStack()
                }
            )
        }

        composable("verificationVideo/{customerId}") { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable
            VerificationVideoScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("deviceList") {
            DeviceListScreen(
                onNavigateToDetail = { deviceId -> navController.navigate("deviceDetail/$deviceId") },
                onNavigateToCreate = { navController.navigate("deviceCreate") }
            )
        }

        composable("deviceCreate") {
            CreateDeviceScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("deviceDetail/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceDetailScreen() // Placeholder
        }

        composable("deviceCondition/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceConditionScreen()
        }

        composable("deviceConditionHistory/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceConditionHistoryScreen()
        }

        composable("deviceInspection/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceInspectionScreen()
        }

        composable("deviceInspectionHistory/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceInspectionHistoryScreen()
        }

        composable("deviceMedia/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceMediaScreen()
        }

        composable("imeiVerification/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            ImeiVerificationScreen()
        }

        composable("deviceLifecycle/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: return@composable
            DeviceLifecycleScreen()
        }

        composable("purchaseList") {
            PurchaseListScreen(
                onNavigateToDetail = { id -> navController.navigate("purchaseDetail/$id") },
                onNavigateToCreate = { navController.navigate("purchaseCreate") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("purchaseCreate") {
            CreatePurchaseScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWizard = { id -> navController.navigate("purchaseDetail/$id") } 
            )
        }

        composable("purchaseDetail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: return@composable
            PurchaseDetailScreen(
                purchaseId = id,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("saleList") {
            SaleListScreen(
                onNavigateToDetail = { id -> navController.navigate("saleDetail/$id") }, // Assuming detail route will be added later
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("inventoryList") {
            InventoryListScreen(
                onNavigateToDetail = { id -> navController.navigate("inventoryDetail/$id") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("networkInventory") {
            NetworkInventoryScreen()
        }

        composable("settlementList") {
            SettlementListScreen()
        }
    }
}
