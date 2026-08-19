package com.superbusiness

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.superbusiness.models.AgentType
import com.superbusiness.services.AgentEngine
import com.superbusiness.services.MonetizationService
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var agentEngine: AgentEngine
    private lateinit var monetizationService: MonetizationService
    private var userId = "user_${System.currentTimeMillis()}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        agentEngine = AgentEngine(this)
        monetizationService = MonetizationService()

        setupUI()
    }

    private fun setupUI() {
        val tvBalance = findViewById<TextView>(R.id.tv_balance)
        val tvTotalEarnings = findViewById<TextView>(R.id.tv_total_earnings)
        val btnCreateAgent = findViewById<Button>(R.id.btn_create_agent)
        val btnWithdraw = findViewById<Button>(R.id.btn_withdraw)
        val etAgentName = findViewById<EditText>(R.id.et_agent_name)
        val rvAgents = findViewById<RecyclerView>(R.id.rv_agents)

        // Update balance display
        updateBalanceDisplay(tvBalance, tvTotalEarnings)

        // Create agent button
        btnCreateAgent.setOnClickListener {
            val agentName = etAgentName.text.toString().ifEmpty { "Agent_${System.currentTimeMillis()}" }
            createAgent(agentName)
            etAgentName.text.clear()
        }

        // Withdraw earnings
        btnWithdraw.setOnClickListener {
            showWithdrawalDialog()
        }
    }

    private fun createAgent(name: String) {
        val agentTypes = listOf(
            AgentType.CONTENT_CREATOR,
            AgentType.AD_VIEWER,
            AgentType.TASK_WORKER
        )
        val randomType = agentTypes.random()

        val agent = agentEngine.createAndRunAgent(userId, randomType, name)
        
        if (agent != null) {
            Toast.makeText(this, "Agent '$name' created and running!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to create agent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBalanceDisplay(tvBalance: TextView, tvTotalEarnings: TextView) {
        val total = monetizationService.getTotalEarnings(userId)
        tvBalance.text = "Balance: $${'$'}${String.format("%.2f", total)}"
        tvTotalEarnings.text = "Total Earnings: $${'$'}${String.format("%.2f", total)}"
    }

    private fun showWithdrawalDialog() {
        val totalEarnings = monetizationService.getTotalEarnings(userId)
        if (totalEarnings < 10.00) {
            Toast.makeText(this, "Minimum withdrawal amount: ${'$'}10.00", Toast.LENGTH_SHORT).show()
            return
        }

        // Show withdrawal dialog (implement as needed)
        Toast.makeText(this, "Withdrawal feature coming soon!", Toast.LENGTH_SHORT).show()
    }
}
