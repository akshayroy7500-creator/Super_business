package com.superbusiness.services

import com.superbusiness.models.*
import java.time.LocalDateTime

class MonetizationService {

    // Track earnings from different sources
    private val earningsTracker = mutableMapOf<String, EarningRecord>()
    private val subscriptions = mutableMapOf<String, SubscriptionRecord>()

    /**
     * Record earnings when an agent completes a task
     */
    fun recordTaskCompletion(
        userId: String,
        agentId: String,
        taskId: String,
        reward: Double
    ): Boolean {
        return try {
            val recordId = "${userId}_${taskId}_${System.currentTimeMillis()}"
            earningsTracker[recordId] = EarningRecord(
                id = recordId,
                userId = userId,
                agentId = agentId,
                amount = reward,
                source = EarningSource.TASK_COMPLETION,
                timestamp = LocalDateTime.now(),
                status = TransactionStatus.COMPLETED
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Process referral commission
     */
    fun recordReferralEarning(
        referrerId: String,
        newUserId: String,
        commissionAmount: Double = 5.00
    ): Boolean {
        return try {
            val recordId = "${referrerId}_ref_${System.currentTimeMillis()}"
            earningsTracker[recordId] = EarningRecord(
                id = recordId,
                userId = referrerId,
                agentId = "",
                amount = commissionAmount,
                source = EarningSource.REFERRAL,
                timestamp = LocalDateTime.now(),
                status = TransactionStatus.COMPLETED,
                metadata = mapOf("referred_user" to newUserId)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Record ad revenue
     */
    fun recordAdRevenue(
        userId: String,
        adType: String,
        impressions: Int
    ): Double {
        val cpm = 2.50  // Cost per 1000 impressions
        val revenue = (impressions / 1000.0) * cpm
        
        val recordId = "${userId}_ad_${System.currentTimeMillis()}"
        earningsTracker[recordId] = EarningRecord(
            id = recordId,
            userId = userId,
            agentId = "",
            amount = revenue,
            source = EarningSource.AD_REVENUE,
            timestamp = LocalDateTime.now(),
            status = TransactionStatus.COMPLETED,
            metadata = mapOf(
                "ad_type" to adType,
                "impressions" to impressions.toString()
            )
        )
        return revenue
    }

    /**
     * Get total earnings for a user
     */
    fun getTotalEarnings(userId: String): Double {
        return earningsTracker
            .filter { it.value.userId == userId }
            .values
            .sumOf { it.amount }
    }

    /**
     * Get earnings breakdown by source
     */
    fun getEarningsBreakdown(userId: String): Map<EarningSource, Double> {
        return earningsTracker
            .filter { it.value.userId == userId }
            .values
            .groupBy { it.source }
            .mapValues { (_, records) -> records.sumOf { it.amount } }
    }

    /**
     * Create subscription for user
     */
    fun createSubscription(
        userId: String,
        tier: AgentTier,
        paymentMethodId: String
    ): SubscriptionRecord? {
        return try {
            val subscription = SubscriptionRecord(
                id = "sub_${System.currentTimeMillis()}",
                userId = userId,
                tier = tier,
                status = SubscriptionStatus.ACTIVE,
                startDate = LocalDateTime.now(),
                renewalDate = LocalDateTime.now().plusMonths(1),
                monthlyPrice = getTierPrice(tier),
                paymentMethodId = paymentMethodId
            )
            subscriptions[subscription.id] = subscription
            subscription
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if user has active subscription
     */
    fun hasActiveSubscription(userId: String): Boolean {
        return subscriptions.values.any {
            it.userId == userId && it.status == SubscriptionStatus.ACTIVE
        }
    }

    /**
     * Get user's current tier
     */
    fun getUserTier(userId: String): AgentTier {
        val subscription = subscriptions.values.find {
            it.userId == userId && it.status == SubscriptionStatus.ACTIVE
        }
        return subscription?.tier ?: AgentTier.BASIC
    }

    /**
     * Process withdrawal request
     */
    fun processWithdrawal(
        userId: String,
        amount: Double,
        bankDetails: String
    ): WithdrawalRecord? {
        val totalEarnings = getTotalEarnings(userId)
        
        if (amount < 10.00 || amount > totalEarnings) {
            return null
        }

        val fee = amount * 0.02  // 2% processing fee
        val netAmount = amount - fee

        return WithdrawalRecord(
            id = "wd_${System.currentTimeMillis()}",
            userId = userId,
            amount = amount,
            netAmount = netAmount,
            fee = fee,
            status = WithdrawalStatus.PROCESSING,
            requestedAt = LocalDateTime.now(),
            processedAt = null,
            bankDetails = bankDetails
        )
    }

    private fun getTierPrice(tier: AgentTier): Double {
        return when (tier) {
            AgentTier.BASIC -> 0.0
            AgentTier.PROFESSIONAL -> 9.99
            AgentTier.ENTERPRISE -> 49.99
        }
    }
}

// Data classes for tracking
data class EarningRecord(
    val id: String,
    val userId: String,
    val agentId: String,
    val amount: Double,
    val source: EarningSource,
    val timestamp: LocalDateTime,
    val status: TransactionStatus,
    val metadata: Map<String, String> = emptyMap()
)

enum class EarningSource {
    TASK_COMPLETION,
    REFERRAL,
    AD_REVENUE,
    SUBSCRIPTION,
    IN_APP_PURCHASE
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}

data class SubscriptionRecord(
    val id: String,
    val userId: String,
    val tier: AgentTier,
    val status: SubscriptionStatus,
    val startDate: LocalDateTime,
    val renewalDate: LocalDateTime,
    val monthlyPrice: Double,
    val paymentMethodId: String
)

enum class SubscriptionStatus {
    ACTIVE,
    PAUSED,
    CANCELLED,
    EXPIRED
}

data class WithdrawalRecord(
    val id: String,
    val userId: String,
    val amount: Double,
    val netAmount: Double,
    val fee: Double,
    val status: WithdrawalStatus,
    val requestedAt: LocalDateTime,
    val processedAt: LocalDateTime?,
    val bankDetails: String
)

enum class WithdrawalStatus {
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}
