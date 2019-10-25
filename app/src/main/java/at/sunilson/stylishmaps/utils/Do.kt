package at.sunilson.stylishmaps.utils

object Do {
    inline infix fun <reified T> exhaustive(any: T?) = any
}