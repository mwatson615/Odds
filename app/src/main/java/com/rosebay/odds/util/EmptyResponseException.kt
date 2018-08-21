package com.rosebay.odds.util

class EmptyResponseException : Exception() {

    override val message: String?
        get() = super.message
}