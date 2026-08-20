package com.superbusiness.solo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SoloMoneyMachineActivity : AppCompatActivity() {

    private lateinit var moneyMachine: SoloMoneyMachine
    private lateinit var tvTotalEarnings: TextView
    private lateinit var tvEarningsRate: TextView
    private lateinit var tvActiveBotsCount: TextView
    private lateinit var tvBreakdown: TextView
    private lateinit var btnStartBots: Button
    private lateinit var btnStopBots: Button
    private lateinit var progressBar: ProgressBar
    
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solo_money_machine)

        moneyMachine = SoloMoneyMachine()
        initializeUI()
        startEarning()
    }

    private fun initializeUI() {
        tvTotalEarnings = findViewById(R.id.tv_total_earnings)
        tvEarningsRate = findViewById(R.id.tv_earnings_rate)
        tvActiveBotsCount = findViewById(R.id.tv_active_bots)
        tvBreakdown = findViewById(R.id.tv_breakdown)
        btnStartBots = findViewById(R.id.btn_start_bots)
        btnStopBots = findViewById(R.id.btn_stop_bots)
        progressBar = findViewById(R.id.progress_bar)

        btnStartBots.setOnClickListener { startEarning() }
        btnStopBots.setOnClickListener { stopEarning() }
    }

    private fun startEarning() {
        uiScope.launch(Dispatchers.Default) {
            moneyMachine.createAutonomousBots(5)
            
            // Update UI every second
            while (true) {
                delay(1000)
                updateUI()
            }
        }
    }

    private fun stopEarning() {
        moneyMachine.stopAllBots()
        btnStartBots.isEnabled = true
        btnStopBots.isEnabled = false
    }

    private suspend fun updateUI() {
        withContext(Dispatchers.Main) {
            val totalEarnings = moneyMachine.getTotalEarnings()
            val earningsRate = moneyMachine.getEarningsRate()
            val activeBots = moneyMachine.getActiveBots()
            val breakdown = moneyMachine.getEarningsByType()

            // Update total earnings
            tvTotalEarnings.text = "Total Earnings: $${String.format("%.2f", totalEarnings)}"
            
            // Update hourly rate
            tvEarningsRate.text = "Earning Rate: $${String.format("%.2f")}/hour"
            
            // Update active bots count
            tvActiveBotsCount.text = "Active Bots: ${activeBots.size}"
            
            // Update breakdown by type
            var breakdownText = "Breakdown:\n"
            breakdown.forEach { (type, amount) ->
                breakdownText += "$type: $${String.format("%.2f", amount)}\n"
            }
            tvBreakdown.text = breakdownText
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        moneyMachine.stopAllBots()
        uiScope.cancel()
    }
}