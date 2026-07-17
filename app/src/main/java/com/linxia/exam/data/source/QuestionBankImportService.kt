package com.linxia.exam.data.source

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.linxia.exam.data.db.AppDatabase
import com.linxia.exam.data.db.entity.Category
import com.linxia.exam.data.db.entity.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class QuestionBankImportService : Service() {
    private val CHANNEL_ID = "import_channel"
    private val NOTIFICATION_ID = 1
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("download_url") ?: ""
        val version = intent?.getStringExtra("version") ?: ""

        if (url.isNotEmpty()) {
            showProgressNotification("开始下载题库...", 0)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    downloadAndImport(url, version)
                } catch (e: Exception) {
                    showErrorNotification("下载失败: ${e.message}")
                    stopSelf()
                }
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "题库导入",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "题库下载和导入进度"
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showProgressNotification(title: String, progress: Int) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText("进度: $progress%")
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun showCompleteNotification(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("题库导入完成")
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    private fun showErrorNotification(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("题库导入失败")
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager?.notify(NOTIFICATION_ID, builder.build())
    }

    private suspend fun downloadAndImport(url: String, version: String) {
        val json = withContext(Dispatchers.IO) {
            val urlObj = URL(url)
            val connection = urlObj.openConnection()
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            BufferedReader(InputStreamReader(connection.getInputStream())).use { reader ->
                reader.readText()
            }
        }

        showProgressNotification("解析数据...", 50)

        val db = AppDatabase.getInstance(this)
        val gson = Json { ignoreUnknownKeys = true }

        withContext(Dispatchers.IO) {
            db.runTransaction {
                // 清空旧数据
                db.questionDao().deleteAll()
                db.categoryDao().deleteAll()

                // 解析并插入分类
                val categoriesData = gson.decodeFromString<List<Category>>(json)
                db.categoryDao().insertAll(categoriesData)

                // 解析并插入题目
                val questionsData = gson.decodeFromString<List<Question>>(json)
                db.questionDao().insertAll(questionsData)

                // 更新题库版本
                // db.questionBankVersionDao().insert(...)
            }
        }

        showProgressNotification("导入完成", 100)
        showCompleteNotification("成功导入 ${questionsData.size} 道题目")
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}