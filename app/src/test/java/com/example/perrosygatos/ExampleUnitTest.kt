package com.example.perrosygatos

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias locales para validar lógica de negocio.
 */
class ExampleUnitTest {

    @Test
    fun emailValidation_isCorrect() {
        // Lógica simple de validación que podríamos usar en la app
        fun isValidEmail(email: String): Boolean {
            return email.isNotEmpty() && email.contains("@") && email.contains(".")
        }

        assertTrue(isValidEmail("test@example.com"))
        assertFalse(isValidEmail("invalid-email"))
        assertFalse(isValidEmail(""))
    }

    @Test
    fun petData_integrity() {
        data class PetTest(val name: String, val type: String)
        val pet = PetTest("Firulais", "Perro")
        
        assertEquals("Firulais", pet.name)
        assertEquals("Perro", pet.type)
    }
}
