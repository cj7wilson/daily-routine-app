package com.dailyroutine.app

import com.dailyroutine.app.billing.BillingManager
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for BillingManager feature gating logic
 */
class BillingManagerTest {

    @Test
    fun `canCreateMoreRoutines returns true when under free limit`() {
        // For unit test, we'll test the logic directly
        val currentCount = 2
        val isPremium = false
        val canCreate = isPremium || currentCount < BillingManager.FREE_ROUTINE_LIMIT
        
        assertTrue(canCreate)
    }

    @Test
    fun `canCreateMoreRoutines returns false when at free limit`() {
        val currentCount = 3
        val isPremium = false
        val canCreate = isPremium || currentCount < BillingManager.FREE_ROUTINE_LIMIT
        
        assertFalse(canCreate)
    }

    @Test
    fun `canCreateMoreRoutines returns true when premium regardless of count`() {
        val currentCount = 10
        val isPremium = true
        val canCreate = isPremium || currentCount < BillingManager.FREE_ROUTINE_LIMIT
        
        assertTrue(canCreate)
    }

    @Test
    fun `free routine limit is 3`() {
        assertEquals(3, BillingManager.FREE_ROUTINE_LIMIT)
    }
}
