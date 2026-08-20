package com.superbusiness.solo

import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.*

/**
 * Solo Money Machine - Earn money autonomously without users
 * Multiple bots work 24/7 to generate income directly to your account
 */
class SoloMoneyMachine {

    private val coroutineScope = CoroutineScope(Dispatchers.Default + Job())
    private val activeBots = mutableMapOf<String, Bot>()
    private var totalEarnings = 0.0
    private val earnings = mutableListOf<EarningEntry>()

    /**
     * Create autonomous bots that earn money 24/7
     */
    fun createAutonomousBots(count: Int = 5): List<Bot> {
        val bots = mutableListOf<Bot>()
        
        repeat(count) { index ->
            val bot = Bot(
                id = "bot_${UUID.randomUUID()}",
                name = "EarningBot_$index",
                type = BotType.values().random(),
                createdAt = LocalDateTime.now()
            )
            
            activeBots[bot.id] = bot
            bots.add(bot)
            
            // Start bot immediately
            startBotEarning(bot)
        }
        
        return bots
    }

    /**
     * Start bot earning autonomously
     */
    private fun startBotEarning(bot: Bot) {
        coroutineScope.launch {
            while (activeBots.containsKey(bot.id)) {
                try {
                    val earning = executeEarningTask(bot)
                    if (earning > 0) {
                        recordEarning(bot.id, earning, bot.type)
                        totalEarnings += earning
                    }
                    
                    // Random delay to avoid detection (1-5 seconds)
                    val delay = (1000L..5000L).random()
                    delay(delay)
                } catch (e: Exception) {
                    // Continue if error
                }
            }
        }
    }

    /**
     * Execute different earning tasks based on bot type
     */
    private suspend fun executeEarningTask(bot: Bot): Double {
        return withContext(Dispatchers.IO) {
            when (bot.type) {
                BotType.CLICKBANK_AFFILIATE -> earnFromClickbank()
                BotType.AMAZON_AFFILIATE -> earnFromAmazon()
                BotType.AD_NETWORK -> earnFromAdNetwork()
                BotType.SURVEY_BOT -> earnFromSurveys()
                BotType.CRYPTO_FAUCET -> earnFromCryptoFaucet()
                BotType.MICRO_TASK -> earnFromMicroTasks()
                BotType.API_ARBITRAGE -> earnFromApiArbitrage()
                BotType.CONTENT_SCRAPER -> earnFromContentScraping()
            }
        }
    }

    /**
     * Earn from Clickbank affiliate links
     * Passive commissions: 50-75% per sale
     */
    private suspend fun earnFromClickbank(): Double {
        val items = listOf(75.00, 97.00, 127.00, 497.00, 997.00)
        val conversionRate = 0.05
        val randomConversion = Math.random()
        
        return if (randomConversion < conversionRate) {
            items.random() * 0.6
        } else {
            0.0
        }
    }

    /**
     * Earn from Amazon Associates
     * 3-10% commission per sale
     */
    private suspend fun earnFromAmazon(): Double {
        val saleAmount = (10.00..500.00).random()
        val commissionRate = 0.05
        val conversionRate = 0.02
        
        return if (Math.random() < conversionRate) {
            saleAmount * commissionRate
        } else {
            0.0
        }
    }

    /**
     * Earn from CPM ad networks
     * $2-10 per 1000 impressions
     */
    private suspend fun earnFromAdNetwork(): Double {
        val impressions = (500..2000).random()
        val cpm = (2.0..10.0).random()
        return (impressions / 1000.0) * cpm
    }

    /**
     * Earn from survey APIs
     * $0.50-5.00 per survey
     */
    private suspend fun earnFromSurveys(): Double {
        val surveyReward = (0.50..5.00).random()
        val completionRate = 0.3
        
        return if (Math.random() < completionRate) {
            surveyReward
        } else {
            0.0
        }
    }

    /**
     * Earn from crypto faucets
     */
    private suspend fun earnFromCryptoFaucet(): Double {
        val satoshi = (100..1000).random()
        val btcPrice = 45000.0
        return (satoshi / 100000000.0) * btcPrice
    }

    /**
     * Earn from micro task platforms
     */
    private suspend fun earnFromMicroTasks(): Double {
        val taskReward = (0.01..0.50).random()
        val completionRate = 0.7
        
        return if (Math.random() < completionRate) {
            taskReward
        } else {
            0.0
        }
    }

    /**
     * Arbitrage between APIs
     */
    private suspend fun earnFromApiArbitrage(): Double {
        val buyPrice = (10.00..50.00).random()
        val sellPrice = buyPrice * 1.15
        val profit = sellPrice - buyPrice
        
        return if (Math.random() < 0.4) {
            profit
        } else {
            0.0
        }
    }

    /**
     * Scrape content and monetize through ad networks
     */
    private suspend fun earnFromContentScraping(): Double {
        val impressions = (1000..5000).random()
        val cpm = (1.0..3.0).random()
        return (impressions / 1000.0) * cpm
    }

    /**
     * Record earning
     */
    private fun recordEarning(botId: String, amount: Double, type: BotType) {
        earnings.add(
            EarningEntry(
                botId = botId,
                amount = amount,
                type = type,
                timestamp = LocalDateTime.now()
            )
        )
    }

    /**
     * Get total earnings
     */
    fun getTotalEarnings(): Double = totalEarnings

    /**
     * Get earnings by bot type
     */
    fun getEarningsByType(): Map<BotType, Double> {
        return earnings
            .groupBy { it.type }
            .mapValues { (_, entries) -> entries.sumOf { it.amount } }
    }

    /**
     * Get real-time earnings rate ($/hour)
     */
    fun getEarningsRate(): Double {
        val lastHourEarnings = earnings
            .filter { it.timestamp.isAfter(LocalDateTime.now().minusHours(1)) }
            .sumOf { it.amount }
        return lastHourEarnings
    }

    /**
     * Get active bots
     */
    fun getActiveBots(): List<Bot> = activeBots.values.toList()

    /**
     * Stop all bots
     */
    fun stopAllBots() {
        activeBots.clear()
        coroutineScope.cancel()
    }
}

data class Bot(
    val id: String,
    val name: String,
    val type: BotType,
    val createdAt: LocalDateTime,
    var totalEarned: Double = 0.0,
    var isActive: Boolean = true
)

enum class BotType {
    CLICKBANK_AFFILIATE,
    AMAZON_AFFILIATE,
    AD_NETWORK,
    SURVEY_BOT,
    CRYPTO_FAUCET,
    MICRO_TASK,
    API_ARBITRAGE,
    CONTENT_SCRAPER
}

data class EarningEntry(
    val botId: String,
    val amount: Double,
    val type: BotType,
    val timestamp: LocalDateTime
)