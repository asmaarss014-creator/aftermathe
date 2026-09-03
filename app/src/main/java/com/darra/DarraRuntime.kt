package com.darra.app

object DarraRuntime {
    data class Validation(val ok: Boolean, val error: String? = null)

    fun validate(source: String): Validation {
        if (source.isBlank()) return Validation(false, "Code is empty")
        if (source.length > 2_000_000) return Validation(false, "Source is too large")

        var balance = 0
        for (c in source) {
            when (c) {
                '(', '[', '{' -> balance++
                ')', ']', '}' -> balance--
            }
            if (balance < 0) return Validation(false, "Unbalanced brackets")
        }
        if (balance != 0) return Validation(false, "Unbalanced brackets")

        return Validation(true)
    }
}
