package com.example.moviebooktracker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val itemList = mutableListOf<Item>()
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val recyclerView = findViewById<RecyclerView>(R.id.item_list)

        adapter = MyAdapter(itemList) { item, position ->
            showItemDetailsDialog(item, position)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputName = findViewById<EditText>(R.id.item_name)
        val inputGenre = findViewById<EditText>(R.id.item_genre)
        val inputDesc = findViewById<EditText>(R.id.item_desc)
        val inputScore = findViewById<SeekBar>(R.id.item_score)
        val inputType = findViewById<RadioGroup>(R.id.item_type)
        val submitButton = findViewById<Button>(R.id.submit_button)

        val inputScoreValue = findViewById<TextView>(R.id.seekbar_score_value)

        inputScore.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                inputScoreValue.text = p1.toString()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        submitButton.setOnClickListener{
            val name = inputName.text.toString().trim()
            val genre = inputGenre.text.toString().trim()
            val desc = inputDesc.text.toString().trim()
            val score = inputScore.progress
            val type = when(inputType.checkedRadioButtonId){
                R.id.item_book -> "Książka"
                R.id.item_movie -> "Film"
                else -> null
            }

            val iconRes = when(type){
                "Książka" -> R.drawable.book
                "Film" -> R.drawable.movie
                else -> R.drawable.question_mark
            }

            if(name.isEmpty() || genre.isEmpty() || desc.isEmpty() || type == null){
                Toast.makeText(this, "Wypełnij wszystkie pola!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val newItem = Item(
                name = name,
                genre = genre,
                desc = desc,
                score = score,
                type = type,
                isDone = false,
                iconRes = iconRes
            )

            itemList.add(newItem)
            adapter.notifyItemInserted(itemList.size - 1)

            inputName.text.clear()
            inputGenre.text.clear()
            inputDesc.text.clear()
            inputScore.progress = 0
            inputType.clearCheck()
        }

        val saveButton = findViewById<Button>(R.id.save_to_gson_button)

        saveButton.setOnClickListener{
            try{
                ItemJsonManager.saveItemListToJson(this, itemList)
                Toast.makeText(this, "Zapisano dane do pliku", Toast.LENGTH_LONG).show()
            }
            catch(ex: Exception){
                Log.e("save", "Wystąpił błąd podczas zapisywania: $ex")
            }
        }

        val loadButton = findViewById<Button>(R.id.pull_gson_button)

        loadButton.setOnClickListener {
            try{
                val loadedItemList = ItemJsonManager.loadItemListFromJson(this)
                Toast.makeText(this, "Wczytano ${loadedItemList.size} elementów.", Toast.LENGTH_LONG).show()
                itemList.clear()
                itemList.addAll(loadedItemList)
                adapter.notifyDataSetChanged()
            }
            catch(ex: Exception){
                Log.e("load", "Cos poszło nie tak: $ex")
            }
        }
    }

    private fun showItemDetailsDialog(item: Item, position: Int){
        var builder = AlertDialog.Builder(this)
        builder.setTitle(item.name)
        builder.setMessage("""
            Nazwa: ${item.name},
            Rodzaj: ${item.type}
            Gatunek: ${item.genre}
            Opis: ${item.desc}
            Ocena: ${item.score}/10
            Status: ${if (item.isDone) "Ukończono" else "Nie ukończono"}
        """.trimIndent())

        builder.setPositiveButton("OK"){dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("Usuń"){dialog, _ ->
            itemList.removeAt(position)
            adapter.notifyItemRemoved(position)
            Toast.makeText(this, "Usunięto element $position", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        builder.show()
    }
}