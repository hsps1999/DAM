package com.notes.notesproxmlviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    var addNoteBtn: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var menuBtn: ImageButton? = null
    var noteAdapter: NoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteBtn = findViewById(R.id.add_note_btn)
        recyclerView = findViewById(R.id.recyler_view)
        menuBtn = findViewById(R.id.menu_btn)

        addNoteBtn!!.setOnClickListener {
            startActivity(Intent(this, NoteDetailsActivity::class.java))
        }
        menuBtn!!.setOnClickListener { showMenu() }
    }

    override fun onStart() {
        super.onStart()
        fetchNotesFromFirebase()
    }

    private fun fetchNotesFromFirebase() {
        Utility.getCollectionReferenceForNotes()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Utility.showToast(this, "Failed to load notes")
                    return@addOnCompleteListener
                }
                val notes = mutableListOf<Note>()
                val docIds = mutableListOf<String>()
                for (doc in task.result) {
                    val note = doc.toObject(Note::class.java)
                    notes.add(note)
                    docIds.add(doc.id)
                }
                noteAdapter = NoteAdapter(this, notes, docIds) { note, docId ->
                    val intent = Intent(this, NoteDetailsActivity::class.java)
                    intent.putExtra("title", note.title)
                    intent.putExtra("content", note.content)
                    intent.putExtra("docId", docId)
                    startActivity(intent)
                }
                recyclerView!!.layoutManager = LinearLayoutManager(this)
                recyclerView!!.adapter = noteAdapter
            }
    }

    fun showMenu() {
        val popupMenu = android.widget.PopupMenu(this, menuBtn)
        popupMenu.menu.add("Sign out")
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.title == "Sign out") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            } else false
        }
        popupMenu.show()
    }
}
