package com.griffin.core.network.json

import com.alibaba.fastjson2.JSONB
import com.alibaba.fastjson2.JSONReader
import com.alibaba.fastjson2.JSONWriter
import com.alibaba.fastjson2.SymbolTable
import com.alibaba.fastjson2.filter.Filter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class FastJsonConfig {

    /**
     * Default charset
     */
    var charset: Charset = StandardCharsets.UTF_8

    /**
     * Format date type
     */
    var dateFormat: String = "yyyy-MM-dd HH:mm:ss"

    /**
     * JSONReader Features
     */
    var readerFeatures: Array<JSONReader.Feature> = emptyArray()

    /**
     * JSONWriter Features
     */
    var writerFeatures: Array<JSONWriter.Feature> = arrayOf(
        JSONWriter.Feature.WriteByteArrayAsBase64,
        JSONWriter.Feature.BrowserSecure
    )

    /**
     * JSONReader Filters
     */
    var readerFilters: Array<Filter> = emptyArray()

    /**
     * JSONWriter Filters
     */
    var writerFilters: Array<Filter> = emptyArray()

    /**
     * Write content length
     */
    var writeContentLength: Boolean = true

    /**
     * JSONB flag
     */
    var isJSONB: Boolean = false

    /**
     * JSONB symbol table
     */
    var symbolTable: SymbolTable? = null

    /**
     * Sets the JSONB symbol table with names.
     */
    fun setSymbolTable(vararg names: String) {
        symbolTable = JSONB.symbolTable(*names)
    }
}
