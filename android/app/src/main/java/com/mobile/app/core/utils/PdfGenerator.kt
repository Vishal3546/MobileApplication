package com.mobile.app.core.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.mobile.app.domain.model.purchase.Purchase
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

object PdfGenerator {

    fun generateReceiptPdf(context: Context, purchase: Purchase): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Header
        paint.textSize = 20f
        paint.color = Color.BLACK
        paint.isFakeBoldText = true
        canvas.drawText("Purchase Receipt", 50f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Purchase #: ${purchase.purchaseNumber}", 50f, 80f, paint)
        canvas.drawText("Date: ${purchase.createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))}", 50f, 100f, paint)

        // Line
        paint.strokeWidth = 2f
        canvas.drawLine(50f, 120f, 545f, 120f, paint)

        // Device Info
        paint.isFakeBoldText = true
        canvas.drawText("Device Details", 50f, 150f, paint)
        paint.isFakeBoldText = false
        canvas.drawText("Brand: ${purchase.device?.brand ?: "N/A"}", 50f, 175f, paint)
        canvas.drawText("Model: ${purchase.device?.model ?: "N/A"}", 50f, 195f, paint)
        canvas.drawText("IMEI: ${purchase.device?.imei1 ?: "N/A"}", 50f, 215f, paint)

        // Customer Info
        paint.isFakeBoldText = true
        canvas.drawText("Customer Details", 50f, 255f, paint)
        paint.isFakeBoldText = false
        canvas.drawText("Name: ${purchase.customer?.firstName ?: "N/A"} ${purchase.customer?.lastName ?: ""}", 50f, 280f, paint)
        canvas.drawText("Phone: ${purchase.customer?.phone ?: "N/A"}", 50f, 300f, paint)

        // Payment Info
        paint.isFakeBoldText = true
        canvas.drawText("Payment Summary", 50f, 340f, paint)
        paint.isFakeBoldText = false
        canvas.drawText("Final Price:", 50f, 365f, paint)
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("₹${purchase.finalPrice}", 400f, 365f, paint)

        pdfDocument.finishPage(page)

        val filePath = File(context.cacheDir, "Receipt_${purchase.purchaseNumber}.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }

        return filePath
    }
}
