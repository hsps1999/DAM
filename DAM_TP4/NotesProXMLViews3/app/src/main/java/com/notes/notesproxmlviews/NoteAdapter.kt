package com.notes.notesproxmlviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val context: Context,
    private val notes: List<Note>,
    private val docIds: List<String>,
    private val onNoteClick: (Note, String) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        val docId = docIds[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.timestampTextView.text = if (note.timestamp != null)
            Utility.timestampToString(note.timestamp) else ""
        holder.itemView.setOnClickListener { onNoteClick(note, docId) }
    }

    override fun getItemCount() = notes.size
}
