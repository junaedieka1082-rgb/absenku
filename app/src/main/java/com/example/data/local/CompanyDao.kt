package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CompanyPolicy
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM company_policy WHERE id = 1 LIMIT 1")
    fun getCompanyPolicy(): Flow<CompanyPolicy?>

    @Query("SELECT * FROM company_policy WHERE id = 1 LIMIT 1")
    suspend fun getCompanyPolicySync(): CompanyPolicy?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePolicy(policy: CompanyPolicy)

    @Update
    suspend fun updatePolicy(policy: CompanyPolicy)
}
