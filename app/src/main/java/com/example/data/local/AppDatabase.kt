package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Announcement
import com.example.data.model.AttendanceRecord
import com.example.data.model.CompanyPolicy
import com.example.data.model.Employee
import com.example.data.model.LeaveRequest
import com.example.data.model.PayrollRecord

@Database(
    entities = [
        Employee::class,
        AttendanceRecord::class,
        LeaveRequest::class,
        PayrollRecord::class,
        CompanyPolicy::class,
        Announcement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun leaveDao(): LeaveDao
    abstract fun payrollDao(): PayrollDao
    abstract fun companyDao(): CompanyDao
    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "presensipro_enterprise.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
