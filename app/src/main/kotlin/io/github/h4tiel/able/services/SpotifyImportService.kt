package io.github.h4tiel.able.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.h4tiel.able.R
import io.github.h4tiel.able.models.Song
import io.github.h4tiel.able.models.SongState
import io.github.h4tiel.able.utils.Constants
import io.github.h4tiel.able.utils.SpotifyImport
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The service that handles import of Spotify songs.
 */
@ExperimentalCoroutinesApi
class SpotifyImportService(val context: Context, workerParams: WorkerParameters) : MusicService.MusicClient, Worker (
    context,
    workerParams
) {
    private lateinit var builder: Notification.Builder
    private lateinit var notification: Notification
    override fun doWork(): Result {
        createNotificationChannel()
        if (runAttemptCount > 2) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
                it.cancel(3)
            }
            return Result.failure()
        }
        return try {
            val playId = inputData.getString("inputId")
            MusicService.registeredClients.forEach { it.spotifyImportChange(true) }
            NotificationManagerCompat.from(context).apply {
                builder.setContentText("")
                builder.setContentTitle(context.getString(R.string.init_import))
                builder.setProgress(100, 100, true)
                builder.setOngoing(true)
                notify(3, builder.build())
            }
            SpotifyImport.importList(playId.toString(), builder, context)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    override fun onStopped() {
        SpotifyImport.isImporting = false
        super.onStopped()
        MusicService.unregisterClient(this)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).also {
            it.cancel(3)
        }

    }

    private fun createNotificationChannel() {
        MusicService.registerClient(this)
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, Constants.CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(context)
        }
        builder.apply {
            setContentTitle(context.getString(R.string.init_import))
            setContentText(context.getString(R.string.pl_wait))
            setSubText("Spotify ${context.getString(R.string.imp)}")
            setSmallIcon(R.drawable.ic_download_icon)
            builder.setOngoing(true)
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Constants.CHANNEL_ID,
                "AbleMusicImport",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = builder.setChannelId(Constants.CHANNEL_ID).build()
        } else {
            notification = builder.build()
            notificationManager.notify(3, notification)
        }
    }

    override fun playStateChanged(state: SongState) {
        
    }

    override fun songChanged() {
        
    }

    override fun durationChanged(duration: Int) {
        
    }

    override fun isExiting() {
        this.stop()
    }

    override fun queueChanged(arrayList: ArrayList<Song>) {
        
    }

    override fun shuffleRepeatChanged(onShuffle: Boolean, onRepeat: Boolean) {
        
    }

    override fun indexChanged(index: Int) {
        
    }

    override fun isLoading(doLoad: Boolean) {
        
    }

    override fun spotifyImportChange(starting: Boolean) {
        if(!starting) {
            this.stop()
            SpotifyImport.isImporting = false
        }
    }

    override fun serviceStarted() {}
}