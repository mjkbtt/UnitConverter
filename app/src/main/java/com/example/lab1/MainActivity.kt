package com.example.lab1

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private val categories = listOf(
        "Length", "Area", "Volume", "Mass",
        "Time", "Speed", "Temperature", "Density",
        "Energy", "Angle", "Weight", "Fuel"
    )

    private val iconByCategory = mapOf(
        "Length" to R.drawable.ic_length,
        "Area" to R.drawable.ic_area,
        "Volume" to R.drawable.ic_volume,
        "Mass" to R.drawable.ic_mass,
        "Time" to R.drawable.ic_time,
        "Speed" to R.drawable.ic_speed,
        "Temperature" to R.drawable.ic_temperature,
        "Density" to R.drawable.ic_density,
        "Energy" to R.drawable.ic_energy,
        "Angle" to R.drawable.ic_angle,
        "Weight" to R.drawable.ic_weight,
        "Fuel" to R.drawable.ic_fuel
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left + dp(18), bars.top, bars.right + dp(18), bars.bottom)
            insets
        }

        renderCategories("")

        findViewById<EditText>(R.id.searchEditText)
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    renderCategories(s?.toString().orEmpty())
                }

                override fun afterTextChanged(s: Editable?) = Unit
            })
    }

    private fun renderCategories(query: String) {
        val grid = findViewById<LinearLayout>(R.id.categoryGrid)
        grid.removeAllViews()

        val matches = categories.filter {
            it.contains(query.trim(), ignoreCase = true)
        }

        matches.chunked(2).forEach { pair ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            pair.forEach { categoryName ->
// card үүсгэх
                val card = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setBackgroundResource(R.drawable.bg_category_card)

                    val icon = ImageView(this@MainActivity).apply {
                        setImageResource(iconByCategory.getValue(categoryName))
                        imageTintList = ColorStateList.valueOf(
                            this@MainActivity.getColor(R.color.primary_blue)
                        )
                        contentDescription = categoryName
                    }
                    addView(icon, LinearLayout.LayoutParams(dp(36), dp(36)))

                    val label = TextView(this@MainActivity).apply {
                        text = categoryName
                        textSize = 15f
                        gravity = Gravity.CENTER
                        setTextColor(this@MainActivity.getColor(R.color.primary_blue))
                        setPadding(0, dp(10), 0, 0)
                    }
                    addView(label)
                }

// Дарсан тохиолдолд activity шилжинэ
                card.setOnClickListener {
                    val intent = Intent(this@MainActivity, ConverterActivity::class.java)
                    intent.putExtra("category_name", categoryName)
                    startActivity(intent)
                }

                val position = LinearLayout.LayoutParams(0, dp(112), 1f).apply {
                    setMargins(dp(6), dp(6), dp(6), dp(6))
                }
                row.addView(card, position)
            }

            if (pair.size == 1) {
                row.addView(View(this), LinearLayout.LayoutParams(0, 1, 1f))
            }
            grid.addView(row)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()
}
