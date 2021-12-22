

package io.github.h4tiel.able.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import io.github.h4tiel.able.R
import io.github.h4tiel.able.adapters.LocalPlaylistAdapter
import io.github.h4tiel.able.databinding.LocalplaylistBinding
import io.github.h4tiel.able.models.Song
import io.github.h4tiel.able.services.MusicService
import io.github.h4tiel.able.utils.Shared
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import java.lang.ref.WeakReference

/**
 * The activity that shows up when a user taps on a local playlist from the
 * playlist fragment.
 */
@ExperimentalCoroutinesApi
class LocalPlaylist : AppCompatActivity(), CoroutineScope {
    var mService: MutableStateFlow<MusicService?> = MutableStateFlow(null)
    var isBound = false
    private lateinit var serviceConn: ServiceConnection
    private var resultArray = ArrayList<Song>()

    override val coroutineContext = Dispatchers.Main + SupervisorJob()
    private lateinit var binding: LocalplaylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/inter.otf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
        binding = LocalplaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loadingView.progress = 0.3080229f
        binding.loadingView.playAnimation()
        val name = intent.getStringExtra("name") ?: ""

        serviceConn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                mService.value = (service as MusicService.MusicBinder).getService()
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                isBound = false
            }
        }

        binding.playbumName.text = name.replace(".json", "")

        binding.playbumPlay.setOnClickListener {
            var freshStart = false

            if (!Shared.serviceRunning(MusicService::class.java, this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(Intent(this, MusicService::class.java))
                } else {
                    startService(Intent(this, MusicService::class.java))
                }

                bindEvent()
                freshStart = true
            }

            launch(Dispatchers.Default) {
                val playSong = fun(){
                    val mService = mService.value!!
                    mService.setQueue(resultArray)
                    mService.setIndex(0)

                    if(freshStart)
                        MusicService.registeredClients.forEach(MusicService.MusicClient::serviceStarted)
                }

                if(mService.value != null) playSong()
                else {
                    mService.collect {
                        if(it != null) {
                            playSong()
                        }
                    }
                }
            }
        }

        launch(Dispatchers.Default) {
            resultArray = Shared.getSongsFromPlaylistFile(name)

            launch(Dispatchers.Main) {
                binding.apRv.adapter = LocalPlaylistAdapter(resultArray, WeakReference(this@LocalPlaylist))
                binding.apRv.layoutManager = LinearLayoutManager(this@LocalPlaylist)
                binding.loadingView.visibility = View.GONE
                binding.loadingView.pauseAnimation()
                binding.apRv.alpha = 0f
                binding.apRv.visibility = View.VISIBLE
                binding.apRv.animate().alpha(1f).duration = 200
                binding.srPr.alpha = 0f
                binding.srPr.visibility = View.VISIBLE
                binding.srPr.animate().alpha(1f).duration = 200
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(mService.value == null)
            bindEvent()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    private fun bindEvent() {
        if (Shared.serviceRunning(MusicService::class.java, this)) {
            try {
                this.also {
                    it.bindService(Intent(it, MusicService::class.java), serviceConn, 0)
                }
            } catch (e: Exception) {
                Log.e("ERR>", e.toString())
            }
        }
    }

    /**
     * invoked when an item is pressed in the recyclerview.
     */
    fun itemPressed(array: ArrayList<Song>, index: Int) {
        var freshStart = false
        if (!Shared.serviceRunning(MusicService::class.java, this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, MusicService::class.java))
            } else {
                startService(Intent(this, MusicService::class.java))
            }

            bindEvent()
            freshStart = true
        }

        launch(Dispatchers.Default) {
            val playSong = fun(){
                val mService = mService.value!!
                mService.setQueue(array)
                mService.setIndex(index)
                if(freshStart)
                    MusicService.registeredClients.forEach(MusicService.MusicClient::serviceStarted)
            }

            if(mService.value != null) playSong()
            else {
                mService.collect {
                    if(it != null) {
                        playSong()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //Need to fix this, there is no playbum_art in local_playlist.xml
//        if (!this.isDestroyed)
//            Glide.with(this).clear(binding.playbumArt)
    }
}