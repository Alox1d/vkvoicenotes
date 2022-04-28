package com.alox1d.vkvoicenotes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alox1d.vkvoicenotes.R
import com.alox1d.vkvoicenotes.databinding.ItemRecyclerBinding
import com.alox1d.vkvoicenotes.domain.model.VoiceNote
import kotlin.properties.Delegates

class VoiceNotesAdapter(
    val mListener: OnVoiceListAdapterListener
) : RecyclerView.Adapter<VoiceNotesAdapter.NoteViewHolder>() {

    var playingPosition = -1
    var playingViewHolder:NoteViewHolder? = null

    var notes: List<VoiceNote> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = notes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemGiphyBinding: ItemRecyclerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_recycler,
            parent,
            false
        )
        return NoteViewHolder(itemGiphyBinding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.itemBinding.data = notes[position]
        holder.onBind(notes[position], holder, position)
    }

    inner class NoteViewHolder(val itemBinding: ItemRecyclerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun onBind(note: VoiceNote, holder: NoteViewHolder, position: Int) {

            itemView.setOnLongClickListener {
            mListener.removeNoteItem(note)
                true
            }

            itemView.setOnClickListener {
//                if (playingPosition == position) {
//                    playingPosition =  -1
////                    holder.itemBinding.playButton.setImageResource(R.drawable.ic_play_vector)
//                    playingViewHolder = null
//
//                } else {
//                    if (playingPosition == -1){
//                        playingPosition = position
////                        holder.itemBinding.playButton.setImageResource(R.drawable.ic_pause_vector)
//                        playingViewHolder = holder
//                    } else {
//                        playingPosition = position
////                        playingViewHolder?.itemBinding?.playButton?.setImageResource(R.drawable.ic_play_vector)
//
////                        holder.itemBinding.playButton.setImageResource(R.drawable.ic_pause_vector)
//                        playingViewHolder = holder
//                    }
//
//                }
                mListener.toggleNote(note, notes)
            }
            val img = if (note.isPlaying) {
                R.drawable.ic_pause_vector
            } else {
                R.drawable.ic_play_vector
            }
            holder.itemBinding.playButton.setImageResource(img)

        }
    }

}

