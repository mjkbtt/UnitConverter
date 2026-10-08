package com.example.lab1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConverterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_converter)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val receivedName = intent.getStringExtra("category_name")
        val categoryName = receivedName
            ?.takeIf { it in UnitConverter.categories }
            ?: "Length"
        val category = UnitConverter.categories.getValue(categoryName)

        findViewById<TextView>(R.id.tvTitle).text = categoryName
        findViewById<TextView>(R.id.tvBack).setOnClickListener {
            finish()
        }

        val input = findViewById<EditText>(R.id.etInput)
        val result = findViewById<TextView>(R.id.tvResult)
        val fromSpinner = findViewById<Spinner>(R.id.spinnerFrom)
        val toSpinner = findViewById<Spinner>(R.id.spinnerTo)

        val unitNames = category.units.keys.toList()
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_unit_item,
            unitNames
        )
        adapter.setDropDownViewResource(R.layout.spinner_unit_item)

        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter
        fromSpinner.setSelection(unitNames.indexOf(category.defaultFrom))
        toSpinner.setSelection(unitNames.indexOf(category.defaultTo))

        fun updateResult() {
            input.error = null
            val raw = input.text.toString().trim().replace(',', '.')

            if (raw.isEmpty()) {
                result.text = ""
                return
            }

            val number = raw.toDoubleOrNull()
            if (number == null || !number.isFinite()) {
                result.text = ""
                input.error = "Enter a valid number"
                return
            }

            val from = fromSpinner.selectedItem?.toString()
            val to = toSpinner.selectedItem?.toString()
            if (from == null || to == null) {
                result.text = ""
                return
            }

            try {
                val converted = UnitConverter.convert(
                    categoryName, number, from, to
                )
                result.text = UnitConverter.format(converted)
            } catch (error: IllegalArgumentException) {
                result.text = ""
                input.error = error.message
            }
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) = updateResult()

            override fun afterTextChanged(s: Editable?) = Unit
        })

        val unitChangeListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) = updateResult()

            override fun onNothingSelected(parent: AdapterView<*>?) =
                updateResult()
        }

        fromSpinner.onItemSelectedListener = unitChangeListener
        toSpinner.onItemSelectedListener = unitChangeListener
//        unit хооронд нь солих
        findViewById<TextView>(R.id.tvSwap).setOnClickListener {
            val oldFrom = fromSpinner.selectedItemPosition
            val oldTo = toSpinner.selectedItemPosition

            fromSpinner.setSelection(oldTo)
            toSpinner.setSelection(oldFrom)
            updateResult()
        }

    }
}