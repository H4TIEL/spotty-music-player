

package io.github.h4tiel.able.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import io.github.h4tiel.able.R
import io.github.h4tiel.able.activities.LocalPlaylist
import io.github.h4tiel.able.models.Playlist
import io.github.h4tiel.able.utils.Constants
import io.github.h4tiel.able.utils.Shared
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.File

/**
 * Lists the playlists in the playlist fragment.
 */
@ExperimentalCoroutinesApi
class PlaylistAdapter(private var playlists: ArrayList<Playlist>)
    : RecyclerView.Adapter<PlaylistAdapter.PLVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PLVH {
        return PLVH(LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false))
    }

    override fun getItemCount() = playlists.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PLVH, position: Int) {
        val current = playlists[position]
        val songs = Shared.getSongsFromPlaylist(current)
        val context = holder.itemView.context

        holder.playlistNameTv.text = current.name.replace(".json", "")
        holder.numberSongsTv.text = "${current.songs.length()} Songs"

        holder.playlistNameTv.typeface =
            Typeface.createFromAsset(context.assets, "fonts/interbold.otf")

        holder.numberSongsTv.typeface =
            Typeface.createFromAsset(context.assets, "fonts/inter.otf")

        holder.itemView.setOnClickListener {
            holder.getContext().run {
                startActivity(Intent(this, LocalPlaylist::class.java).run {
                    this.putExtra("name", current.name)
                    this
                })
            }
        }

        holder.itemView.setOnLongClickListener {
            val songNames = ArrayList<String>()
            Shared.getSongsFromPlaylist(current).also { for (song in it) songNames.add(song.name) }

            MaterialDialog(holder.itemView.context).show {
                title(text = holder.itemView.context.getString(R.string.rem_song))
                listItems(items = songNames){ _, index, _ ->
                    Shared.removeFromPlaylist(current, songs[index])
                    Toast.makeText(context, "Song removed from playlist", Toast.LENGTH_SHORT).show()
                    playlists = Shared.getPlaylists()
                    notifyDataSetChanged()
                    dismiss()
                }

                negativeButton(text = holder.itemView.context.getString(R.string.rem_playlist)) {
                    File(Constants.playlistFolder.absolutePath + "/" + current.name).delete()
                    playlists = Shared.getPlaylists()
                    notifyDataSetChanged()
                }
            }
            false
        }
    }

    fun update(newPlaylists: ArrayList<Playlist>){
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    class PLVH(itemView: View): RecyclerView.ViewHolder(itemView){
        val playlistNameTv = itemView.findViewById<TextView>(R.id.playlist_name)!!
        val numberSongsTv = itemView.findViewById<TextView>(R.id.number_songs)!!

        fun getContext(): Context = itemView.context
    }
}