package com.linxia.exam

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LinxiaExamApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化工作
    }
}