package com.everonet.demo.miniprograms.util

import android.content.Context
import java.io.File
import java.security.MessageDigest

object FileUtils {

    const val PATH_DOWNLOAD = "/download/"

    @JvmStatic
    fun createNewFile(context: Context, filePath: String, fileName: String): File {
        var file: File? = null
        val path = File("${context.externalCacheDir}$filePath")
        var success = true
        if (!path.exists())
            success = path.mkdirs()
        if (success) {
            file = File(path, fileName)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
        }
        return file!!
    }

    /**
     *  删除文件
     *  filepath 文件路径
     */
    @JvmStatic
    fun delFile(filepath: String): Boolean {
        val file = File(filepath)
        if (!file.exists()) {
            return false
        }
        return file.delete()
    }

    @JvmStatic
    fun exists(filePath: String, fileName: String): Boolean {
        val file = File(filePath, fileName)
        return file.exists()
    }

    @JvmStatic
    fun md5(src: String): String {
        var output = src
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(src.toByteArray(Charsets.UTF_8))
            val bytes = md.digest()
//            output = Hex.bytesToHexString(bytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return output
    }
}