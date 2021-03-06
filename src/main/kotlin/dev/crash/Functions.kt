package dev.crash

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.crash.venly.VenlyNFTInfo
import dev.crash.venly.VenlyNFTMetadata
import dev.crash.venly.VenlyNFTToken
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.*
import java.nio.charset.Charset
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.HashMap
import kotlin.experimental.or


fun URL.get(headerParams: HashMap<String, String> = hashMapOf()): String {
    val con = this.openConnection() as HttpsURLConnection
    con.requestMethod = "GET"
    con.doOutput = true
    con.doInput = true
    con.useCaches = false
    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
    headerParams.forEach {
        con.setRequestProperty(it.key, it.value)
    }
    return con.inputStream.readBytes().toString(Charset.defaultCharset())
}

fun URL.post(params: HashMap<String, Any> = hashMapOf(), headerParams: HashMap<String, String> = hashMapOf(), json: Boolean = true): String {
    return post(dataToString(json, params), headerParams, json)
}

fun URL.post(requestString: String, headerParams: HashMap<String, String> = hashMapOf(), json: Boolean = true): String {
    val con: HttpsURLConnection = this.openConnection() as HttpsURLConnection
    con.requestMethod = "POST"
    con.doOutput = true
    con.doInput = true
    con.setRequestProperty("Content-Type", if(json)"application/json" else "application/x-www-form-urlencoded")
    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
    con.setRequestProperty("Content-Length", requestString.length.toString())
    headerParams.forEach {
        con.setRequestProperty(it.key, it.value)
    }
    con.useCaches = false
    DataOutputStream(con.outputStream).use { dos -> dos.writeBytes(requestString) }
    return BufferedReader(InputStreamReader(con.inputStream)).readText()
}

fun URL.put(params: HashMap<String, Any> = hashMapOf(), headerParams: HashMap<String, String> = hashMapOf(), json: Boolean = true): String {
    return put(dataToString(json, params), headerParams, json)
}

fun URL.put(requestString: String, headerParams: HashMap<String, String> = hashMapOf(), json: Boolean = true): String {
    val con: HttpsURLConnection = this.openConnection() as HttpsURLConnection
    con.requestMethod = "PUT"
    con.doOutput = true
    con.doInput = true
    con.setRequestProperty("Content-Type", if(json)"application/json" else "application/x-www-form-urlencoded")
    con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
    con.setRequestProperty("Content-Length", requestString.length.toString())
    headerParams.forEach {
        con.setRequestProperty(it.key, it.value)
    }
    con.useCaches = false
    DataOutputStream(con.outputStream).use { dos -> dos.writeBytes(requestString) }
    return BufferedReader(InputStreamReader(con.inputStream)).readText()
}

fun HashMap<String, String>.asRequestString(): String {
    val builder = StringBuilder()
    this.forEach {
        builder.append(it.key).append("=").append(it.value).append("&")
    }
    return builder.toString().removeSuffix("&")
}

fun List<Any>.joinToNoSpaceString(): String = this.joinToString{it.toString()}.replace(" ", "")

private fun dataToString(json: Boolean, params: HashMap<String, Any>): String {
    return if(json){
        jacksonObjectMapper().writeValueAsString(params)
    }else {
        params.values.forEach {
            if(it !is String){
                throw InputMismatchException("Nothing else than Strings can be put in POST non-json request!")
            }
        }
        val newParams = hashMapOf<String, String>()
        params.forEach {
            newParams[it.key] = it.value.toString()
        }
        newParams.asRequestString()
    }
}

fun VenlyNFTToken.metadata(): VenlyNFTMetadata = jacksonObjectMapper().readValue(metadata)

fun VenlyNFTInfo.metadata(): VenlyNFTMetadata = jacksonObjectMapper().readValue(metadata)

fun Boolean.toInt(): Int = if(this) 1 else 0

fun JsonNode.toObjString(): String = jacksonObjectMapper().writeValueAsString(this)

inline fun <reified T> JsonNode.getChildObj(name: String) = jacksonObjectMapper().readValue<T>(this[name].toObjString())

class EMPTY

fun Short.toByteArray(): ByteArray = byteArrayOf((this.toInt() ushr 8).toByte(), this.toByte())

fun Int.toByteArrayAsVarInt(): MutableList<Byte> {
    var bvalue = this
    val result = mutableListOf<Byte>()
    do {
        var temp = (bvalue and 127).toByte()
        bvalue = bvalue ushr 7
        if (bvalue != 0) {
            temp = temp or 128.toByte()
        }
        result.add(temp)
    } while (bvalue != 0)
    return result
}

fun Int.toByteArray():  MutableList<Byte> = byteArrayOf(
    (this ushr 24).toByte(), (this ushr 16).toByte(),
    (this ushr 8).toByte(), this.toByte()
).toMutableList()

fun Long.toByteArrayAsVarLong(): MutableList<Byte> {
    var bvalue = this
    val result = mutableListOf<Byte>()
    do {
        var temp = (bvalue and 127).toByte()
        bvalue = bvalue ushr 7
        if (bvalue != 0L) {
            temp = temp or 128.toByte()
        }
        result.add(temp)
    } while (bvalue != 0L)
    return result
}

fun Long.toByteArray(): MutableList<Byte> = byteArrayOf(
    (this ushr 56).toByte(), (this ushr 48).toByte(),
    (this ushr 40).toByte(), (this ushr 32).toByte(), (this ushr 24).toByte(),
    (this ushr 16).toByte(), (this ushr 8).toByte(), this.toByte()
).toMutableList()

inline fun <reified T> JsonNode.getObjectOfList(n: Int): T {
    val list = jacksonObjectMapper().readValue<List<JsonNode>>(this.toObjString())
    return jacksonObjectMapper().readValue(list[n-1].toObjString())
}

fun String.toUTF8ByteArray(): ByteArray = toByteArray(Charset.defaultCharset())