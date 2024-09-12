package com.github.mutoxu_n.splitapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.github.mutoxu_n.splitapp.App
import com.github.mutoxu_n.splitapp.activities.ui.theme.SplitAppTheme
import com.github.mutoxu_n.splitapp.api.API
import com.github.mutoxu_n.splitapp.common.Store
import com.github.mutoxu_n.splitapp.components.receipts.ReceiptDetailDisplay
import com.github.mutoxu_n.splitapp.models.Receipt
import kotlinx.coroutines.launch

class EditReceiptActivity : ComponentActivity() {
    private var apiError: Boolean by mutableStateOf(false)

    companion object {
        private const val TAG = "EditReceiptActivity"
        private const val INTENT_IS_EDIT = "INTENT_IS_EDIT"
        private const val INTENT_RECEIPT_ID = "INTENT_RECEIPT_ID"
        private const val INTENT_RECEIPT_STUFF = "INTENT_RECEIPT_STUFF"
        private const val INTENT_RECEIPT_PAID = "INTENT_RECEIPT_PAID"
        private const val INTENT_RECEIPT_BUYERS = "INTENT_RECEIPT_BUYERS"
        private const val INTENT_RECEIPT_PAYMENT = "INTENT_RECEIPT_PAYMENT"
        private const val INTENT_RECEIPT_REPORTED = "INTENT_RECEIPT_REPORTED"
        private const val INTENT_RECEIPT_TIMESTAMP = "INTENT_RECEIPT_TIMESTAMP"

        fun launch(
            context: Context,
            receipt: Receipt? = null,
            launcher: ActivityResultLauncher<Intent>? = null
        ) {
            // Intent作成
            val intent = Intent(context, EditReceiptActivity::class.java)
            val args = Bundle()

            args.putBoolean(INTENT_IS_EDIT, receipt != null)

            if(receipt != null) {
                val model = receipt.toModel()
                args.putString(INTENT_RECEIPT_ID, model.id)
                args.putString(INTENT_RECEIPT_STUFF, model.stuff)
                args.putString(INTENT_RECEIPT_PAID, model.paid)
                args.putStringArray(INTENT_RECEIPT_BUYERS, model.buyers.toTypedArray())
                args.putInt(INTENT_RECEIPT_PAYMENT, model.payment)
                args.putString(INTENT_RECEIPT_REPORTED, model.reportedBy)
                args.putString(INTENT_RECEIPT_TIMESTAMP, model.timestamp)
            }

            intent.putExtras(args)

            // launch
            if (launcher == null) context.startActivity(intent)
            else launcher.launch(intent)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val members = Store.members.value
        if(members == null) {
            finish()
            return
        }

        val initId: String
        val initStuff: String
        val initPaid: String
        val initBuyers: List<String>
        val initPayment: Int
        val initReported: String
        val timestamp: String?

        if(intent.getBooleanExtra(INTENT_IS_EDIT, false)) {
            initId = intent.getStringExtra(INTENT_RECEIPT_ID) ?: "null"
            initStuff = intent.getStringExtra(INTENT_RECEIPT_STUFF) ?: ""
            initPaid = intent.getStringExtra(INTENT_RECEIPT_PAID) ?: ""
            initBuyers = intent.getStringArrayExtra(INTENT_RECEIPT_BUYERS)?.toList() ?: listOf()
            initPayment = intent.getIntExtra(INTENT_RECEIPT_PAYMENT, -1)
            initReported = intent.getStringExtra(INTENT_RECEIPT_REPORTED) ?: ""
            timestamp = intent.getStringExtra(INTENT_RECEIPT_TIMESTAMP) ?: ""

        } else {
            initId = "null"
            initStuff = ""
            initPaid = ""
            initBuyers = listOf()
            initPayment = 0
            initReported = App.me.value?.name ?: ""
            timestamp = null
        }

        val receipt = Receipt(
            id = initId,
            stuff = initStuff,
            paid = members.find { it.name == initPaid } ?: members[0],
            buyers = initBuyers.map { name -> members.find { it.name == name } ?: members[0] },
            payment = initPayment,
            reportedBy = members.find { it.name == initReported } ?: members[0],
            timestamp = timestamp?.let {
                if(it.isBlank()) null else java.time.LocalDateTime.parse(it)
            } ?: java.time.LocalDateTime.now(),
        )

        setContent {
            SplitAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(7.dp),
                    ) {
                        ReceiptDetailDisplay(
                            receipt = receipt,
                            onValueChanged = {
                                lifecycleScope.launch {
                                    if(intent.getBooleanExtra(INTENT_IS_EDIT, false)) {
                                        editReceipt(it)

                                    } else {
                                        createReceipt(it)
                                    }

                                }
                            },
                            members = members,
                        )
                    }
                }
            }
        }
    }

    private suspend fun createReceipt(receipt: Receipt) {
        apiError = false
        App.roomId.value?.let {
            API().createReceipt(it, receipt.toModel())
                { res -> if (res) finish() else apiError = true }
        }
    }

    private suspend fun editReceipt(receipt: Receipt) {
        apiError = false
        App.roomId.value?.let {
            API().editReceipt(it, receipt.toModel())
            { res -> if (res) finish() else apiError = true }
        }

    }
}