
package com.example.lab1

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterTest {

    private data class Case(
        val category: String,
        val value: Double,
        val from: String,
        val to: String,
        val expected: Double
    )

    @Test
    fun allTwelveCategoriesConvertCorrectly() {
        val cases = listOf(
            Case("Length", 1000.0, "Meter", "Kilometer", 1.0),
            Case("Area", 10000.0, "Square Centimeter", "Square Meter", 1.0),
            Case("Volume", 1.0, "Cubic Meter", "Liter", 1000.0),
            Case("Mass", 1.0, "Kilogram", "Gram", 1000.0),
            Case("Time", 2.0, "Hour", "Minute", 120.0),
            Case("Speed", 36.0, "Kilometer/hour", "Meter/second", 10.0),
            Case("Temperature", -40.0, "Celsius", "Fahrenheit", -40.0),
            Case("Density", 1.0, "Gram/cm³", "Kilogram/m³", 1000.0),
            Case("Energy", 1.0, "Kilowatt-hour", "Joule", 3_600_000.0),
            Case("Angle", -90.0, "Degree", "Radian", -Math.PI / 2),
            Case("Weight", 1.0, "Stone", "Kilogram", 6.35029318),
            Case("Fuel", 20.0, "Kilometer/Liter", "Liter/100 km", 5.0)
        )

        for (case in cases) {
            val actual = UnitConverter.convert(
                case.category, case.value, case.from, case.to
            )
            assertEquals(case.category, case.expected, actual, 0.000001)
        }
    }

    @Test
    fun sameUnitKeepsTheValue() {
        val actual = UnitConverter.convert(
            "Length", 12.34, "Meter", "Meter"
        )
        assertEquals(12.34, actual, 0.0)
    }

    @Test
    fun fahrenheitToKelvin() {
        val actual = UnitConverter.convert(
            "Temperature", 32.0, "Fahrenheit", "Kelvin"
        )
        assertEquals(273.15, actual, 0.000001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativeLengthIsRejected() {
        UnitConverter.convert("Length", -5.0, "Meter", "Kilometer")
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroFuelReciprocalIsRejected() {
        UnitConverter.convert(
            "Fuel", 0.0, "Liter/100 km", "Kilometer/Liter"
        )
    }

    @Test
    fun formattedResultRemovesTrailingZeros() {
        assertEquals("1", UnitConverter.format(1.0))
        assertEquals("1.25", UnitConverter.format(1.25))
    }
}