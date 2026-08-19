package com.superbusiness.models

import java.time.LocalDateTime

data class Agent(
    val id: String,
    val name: String,
    val type: AgentType,
    val description: String,
    val isActive: Boolean = true,
    val tier: AgentTier = AgentTier.BASIC,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val tasksCompleted: Int = 0,
    val totalEarnings: Double = 0.0
)

enum class AgentType {
    CONTENT_CREATOR,
    DATA_PROCESSOR,
    AD_VIEWER,
    TASK_WORKER,
    AFFILIATE_MARKETER,
    SURVEY_TAKER,
    GAME_PLAYER,
    SOCIAL_MEDIA_BOT
}

enum class AgentTier {
    BASIC,      // Free tier
    PROFESSIONAL,  // $9.99/month
    ENTERPRISE  // $49.99/month
}

data class AgentTask(
    val id: String,
    val agentId: String,
    val taskType: String,
    val description: String,
    val reward: Double,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    VERIFIED
}

data class UserAccount(
    val userId: String,
    val username: String,
    val email: String,
    val tier: AgentTier = AgentTier.BASIC,
    val balance: Double = 0.0,
    val totalEarnings: Double = 0.0,
    val agents: List<Agent> = emptyList(),
    val referralCode: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)
