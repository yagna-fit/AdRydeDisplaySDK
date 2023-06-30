package com.adryde.mobile.displaysdk.util

import android.content.Context
import android.os.Environment
import com.adryde.mobile.displaysdk.R
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteStatement
import java.io.*
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec


class AESEncoder {


    companion object {

        /**
         * Replaces this database with a version encrypted with the supplied
         * passphrase, deleting the original. Do not call this while the database
         * is open, which includes during any Room migrations.
         *
         * @param ctxt a Context
         * @param originalFile a File pointing to the database
         * @param passphrase the passphrase from the user
         * @throws IOException
         */
        @Throws(IOException::class)
        open fun encryptDirectDb(ctx: Context,source:String, dest: String): File {
            val originalFile =
               File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/${source}")

            val newFile = File.createTempFile("$dest", "tmp", ctx.cacheDir)

            //val newFileSecond =   File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/aaa_db_enc.sql")

            SQLiteDatabase.loadLibs(ctx)
            if (originalFile.exists()) {

                var db: SQLiteDatabase = SQLiteDatabase.openDatabase(
                    originalFile.absolutePath,
                    "", null, SQLiteDatabase.OPEN_READWRITE
                )
                val version: Int = db.version
                db.close()
                db = SQLiteDatabase.openDatabase(
                    newFile.absolutePath, ctx.getString(R.string.app_name).toByteArray(),
                    null, SQLiteDatabase.OPEN_READWRITE, null, null
                )
                val st: SQLiteStatement =
                    db.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''")
                st.bindString(1, originalFile.absolutePath)
                st.execute()
                db.rawExecSQL("SELECT sqlcipher_export('main', 'plaintext')")
                db.rawExecSQL("DETACH DATABASE plaintext")
                db.version = version
                st.close()
                db.close()
                //originalFile.delete()
               // newFile.renameTo(originalFile)
                return newFile
            } else {
                return  originalFile
                throw FileNotFoundException(originalFile.absolutePath + " not found")
            }
        }

        @Throws(
            IOException::class,
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class
        )
        fun encrypt(ctx: Context,source:String, dest: String) {
            // Here you read the cleartext.
            ///storage/sdcard0/Pictures/bpa_db.sql
            val input =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/$source"

            val output =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/$dest"

            val fis = FileInputStream(input)
            // This stream write the encrypted text. This stream will be wrapped by another stream.
            val fos = FileOutputStream(output)

            // Length is 16 byte
            val sks = SecretKeySpec(ctx.getString(R.string.app_name).toByteArray(), "AES")
            // Create cipher
            val cipher: Cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, sks)
            // Wrap the output stream
            val cos = CipherOutputStream(fos, cipher)
            // Write bytes
            var b: Int
            val d = ByteArray(8)
            while (fis.read(d).also { b = it } != -1) {
                cos.write(d, 0, b)
            }
            // Flush and close streams.
            cos.flush()
            cos.close()
            fis.close()
        }

        @Throws(
            IOException::class,
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class
        )
        fun decrypt(ctx: Context,assetFileName :String, destFolder: File ): File {

            val fileInputstrem= ctx.assets.open(assetFileName)

           // val input = //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/bpa_db_enc.sql"

            val outputDir: File = ctx.cacheDir // context being the Activity pointer

            val outputFile: File = File(destFolder,assetFileName)//File.createTempFile("bpa_db_dec", ".db", outputDir)

           // val output =
           //   Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/bpa_db_dec.sql"

            val fis = fileInputstrem//FileInputStream(input)
            val fos = FileOutputStream(outputFile.absolutePath)
            val sks = SecretKeySpec(ctx.getString(R.string.app_name).toByteArray(), "AES")
            val cipher: Cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, sks)
            val cis = CipherInputStream(fis, cipher)
            var b: Int
            val d = ByteArray(8)
            while (cis.read(d).also { b = it } != -1) {
                fos.write(d, 0, b)
            }
            fos.flush()
            fos.close()
            cis.close()

            return outputFile
        }
    }
}