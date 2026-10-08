package com.example.lab1

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

data class CategoryDefinition(
    val units: Map<String, Double>,
    val defaultFrom: String,
    val defaultTo: String
)

object UnitConverter {
    // Тоо бүр тухайн нэгжийн суурь нэгжтэй харьцах хэмжээ.
    private fun category(
        defaultFrom: String,
        defaultTo: String,
        vararg units: Pair<String, Double>
    ) = CategoryDefinition(linkedMapOf(*units), defaultFrom, defaultTo)

    val categories = linkedMapOf(
        "Length" to category(
            "Meter", "Kilometer",
            "Millimeter" to 0.001,
            "Centimeter" to 0.01,
            "Meter" to 1.0,
            "Kilometer" to 1000.0,
            "Inch" to 0.0254,
            "Foot" to 0.3048,
            "Yard" to 0.9144,
            "Mile" to 1609.344
        ),

        "Area" to category(
            "Square Meter", "Square Kilometer",
            "Square Centimeter" to 0.0001,
            "Square Meter" to 1.0,
            "Square Kilometer" to 1_000_000.0,
            "Square Inch" to 0.00064516,
            "Square Foot" to 0.09290304,
            "Hectare" to 10_000.0,
            "Acre" to 4046.8564224
        ),

        "Volume" to category(
            "Liter", "Cubic Meter",
            "Milliliter" to 0.001,
            "Liter" to 1.0,
            "Cubic Meter" to 1000.0,
            "US Cup" to 0.2365882365,
            "US Pint" to 0.473176473,
            "US Quart" to 0.946352946,
            "US Gallon" to 3.785411784
        ),

        "Mass" to category(
            "Kilogram", "Gram",
            "Kilogram" to 1.0,
            "Gram" to 0.001,
            "Milligram" to 0.000001,
            "Metric Ton" to 1000.0,
            "Pound" to 0.45359237,
            "Ounce" to 0.028349523125
        ),

        "Time" to category(
            "Hour", "Minute",
            "Second" to 1.0,
            "Millisecond" to 0.001,
            "Minute" to 60.0,
            "Hour" to 3600.0,
            "Day" to 86400.0,
            "Week" to 604800.0,
            "Year (365 days)" to 31536000.0
        ),

        "Speed" to category(
            "Kilometer/hour", "Meter/second",
            "Meter/second" to 1.0,
            "Kilometer/hour" to (1.0 / 3.6),
            "Mile/hour" to 0.44704,
            "Foot/second" to 0.3048,
            "Knot" to (1852.0 / 3600.0),
            "Kilometer/second" to 1000.0
        ),

        "Temperature" to category(
            "Celsius", "Fahrenheit",
            "Celsius" to 1.0,
            "Fahrenheit" to 1.0,
            "Kelvin" to 1.0
        ),

        "Density" to category(
            "Kilogram/m³", "Gram/cm³",
            "Kilogram/m³" to 1.0,
            "Gram/cm³" to 1000.0,
            "Gram/L" to 1.0,
            "Kilogram/L" to 1000.0,
            "Pound/ft³" to 16.01846337396,
            "Pound/in³" to 27679.9047102
        ),

        "Energy" to category(
            "Joule", "Kilojoule",
            "Joule" to 1.0,
            "Kilojoule" to 1000.0,
            "Megajoule" to 1_000_000.0,
            "Calorie" to 4.184,
            "Kilocalorie" to 4184.0,
            "Watt-hour" to 3600.0,
            "Kilowatt-hour" to 3_600_000.0
        ),

        "Angle" to category(
            "Degree", "Radian",
            "Degree" to 1.0,
            "Radian" to (180.0 / Math.PI),
            "Gradian" to 0.9,
            "Arcminute" to (1.0 / 60.0),
            "Arcsecond" to (1.0 / 3600.0),
            "Revolution" to 360.0
        ),

        "Weight" to category(
            "Kilogram", "Pound",
            "Kilogram" to 1.0,
            "Gram" to 0.001,
            "Metric Ton" to 1000.0,
            "Pound" to 0.45359237,
            "Ounce" to 0.028349523125,
            "Stone" to 6.35029318
        ),

        "Fuel" to category(
            "Kilometer/Liter", "Liter/100 km",
            "Kilometer/Liter" to 1.0,
            "Liter/100 km" to 1.0,
            "Mile/US Gallon" to (1.609344 / 3.785411784),
            "Mile/UK Gallon" to (1.609344 / 4.54609),
            "Mile/Liter" to 1.609344,
            "Kilometer/US Gallon" to (1.0 / 3.785411784)
        )
    )

    fun convert(
        categoryName: String,
        value: Double,
        from: String,
        to: String
    ): Double {
        require(value.isFinite()) { "Enter a valid number" }

        // Таны хүссэнээр Angle-д сөрөг утга зөвшөөрнө.
        require(
            value >= 0 || categoryName == "Temperature" || categoryName == "Angle"
        ) { "Negative values are not allowed for this category" }

        val category = categories[categoryName]
            ?: throw IllegalArgumentException("Unknown category")

        val fromFactor = category.units[from]
            ?: throw IllegalArgumentException("Unknown From unit")
        val toFactor = category.units[to]
            ?: throw IllegalArgumentException("Unknown To unit")

        if (from == to) return value

        val answer = when (categoryName) {
            "Temperature" -> {
                val celsius = when (from) {
                    "Celsius" -> value
                    "Fahrenheit" -> (value - 32.0) * 5.0 / 9.0
                    else -> value - 273.15 // Kelvin
                }

                when (to) {
                    "Celsius" -> celsius
                    "Fahrenheit" -> celsius * 9.0 / 5.0 + 32.0
                    else -> celsius + 273.15 // Kelvin
                }
            }

            "Fuel" -> {
                val kmPerLiter = if (from == "Liter/100 km") {
                    100.0 / value
                } else {
                    value * fromFactor
                }

                if (to == "Liter/100 km") {
                    100.0 / kmPerLiter
                } else {
                    kmPerLiter / toFactor
                }
            }

            else -> {
                val baseValue = value * fromFactor
                baseValue / toFactor
            }
        }

        require(answer.isFinite()) { "Result is outside the supported range" }
        return answer
    }

    fun format(value: Double): String {
        if (value == 0.0) return "0"

        val pattern = if (abs(value) < 0.000001 || abs(value) >= 1e12) {
            "0.######E0"
        } else {
            "0.######"
        }

        return DecimalFormat(
            pattern,
            DecimalFormatSymbols(Locale.US)
        ).format(value)
    }
}