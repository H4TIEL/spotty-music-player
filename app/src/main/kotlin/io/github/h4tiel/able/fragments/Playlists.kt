

package io.github.h4tiel.able.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.getInputLayout
import com.afollestad.materialdialogs.input.input
import io.github.h4tiel.able.R
import io.github.h4tiel.able.adapters.PlaylistAdapter
import io.github.h4tiel.able.databinding.PlaylistsBinding
import io.github.h4tiel.able.models.Song
import io.github.h4tiel.able.models.SongState
import io.github.h4tiel.able.services.MusicService
import io.github.h4tiel.able.services.SpotifyImportService
import io.github.h4tiel.able.utils.Shared
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * The third fragment. Used to view/edit/play locally stored playlists.
 * Playlists are stored in the JSON format.
 */
@ExperimentalCoroutinesApi
class Playlists : Fragment(), MusicService.MusicClient {
    private var isImporting = false
    private var _binding: PlaylistsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MusicService.registerClient(this)
        _binding = PlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding!!.playlistsRv.adapter = PlaylistAdapter(Shared.getPlaylists())
        _binding!!.playlistsRv.layoutManager = LinearLayoutManager(requireContext())
        var inputId = ""
        _binding!!.spotbut.setOnClickListener {
            if (!isImporting) {
                MaterialDialog(requireContext()).show {
                    title(R.string.spot_title)
                    input(waitForPositiveButton = false) { dialog, textInp ->
                        val inputField = dialog.getInputField()
                        val validUrl =
                            textInp.toString().replace("https://", "")
                                .split("/").toMutableList()
                        var isValid = true
                        if (validUrl.size <= 2 || validUrl[0] != "open.spotify.com" || validUrl[1] != "playlist") {
                            isValid = false
                        } else if (validUrl[2].contains("?")) {
                            inputId = validUrl[2].split("?")[0]
                        } else {
                            inputId = validUrl[2]
                        }

                        inputField.error = if (isValid) null else getString(R.string.spot_invalid)
                        dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
                    }
                    getInputLayout().boxBackgroundColor = Color.parseColor("#212121")
                    positiveButton(R.string.pos) {
                        val builder = Data.Builder()
                        builder.put("inputId", inputId)
                        WorkManager.getInstance(view.context)
                            .beginUniqueWork(
                                "SpotifyImport",
                                ExistingWorkPolicy.REPLACE,
                                OneTimeWorkRequest.Builder(SpotifyImportService::class.java)
                                    .setInputData(builder.build())
                                    .build()
                            ).enqueue()
                        Toast.makeText(
                            requireContext(), getString(R.string.spot_importing), Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                MusicService.registeredClients.forEach { it.spotifyImportChange(false) }
                WorkManager.getInstance(view.context).cancelUniqueWork("SpotifyImport")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicService.unregisterClient(this)
        _binding = null
    }

    override fun playStateChanged(state: SongState) {}

    override fun songChanged() {}

    override fun durationChanged(duration: Int) {}

    override fun isExiting() {}

    override fun queueChanged(arrayList: ArrayList<Song>) {}

    override fun shuffleRepeatChanged(onShuffle: Boolean, onRepeat: Boolean) {}

    override fun indexChanged(index: Int) {}

    override fun isLoading(doLoad: Boolean) {}

    override fun spotifyImportChange(starting: Boolean) {
        if(starting){
            isImporting = true
            _binding!!.spotbut.setImageResource(R.drawable.ic_cancle_action)
        } else {
            isImporting = false
            WorkManager.getInstance(requireContext()).cancelUniqueWork("SpotifyImport")
            (activity?.findViewById<RecyclerView>(R.id.playlists_rv)?.adapter as PlaylistAdapter).also { playlistAdapter ->
                playlistAdapter.update(Shared.getPlaylists())
                _binding!!.spotbut.setImageResource(R.drawable.ic_spot)
            }
        }
    }

    override fun serviceStarted() {}
}