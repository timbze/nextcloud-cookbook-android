package de.micmun.android.nextcloudcookbook.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.lang.Exception
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

class Crypto {
    companion object {

        private const val TAG = "Crypto"
        private const val AndroidKeyStore = "AndroidKeyStore"
        private const val AES_MODE = "AES/GCM/NoPadding"
        private const val KEY_ALIAS = "secureKeyCookbook"
        private const val FIXED_IV = "eleY=:@0/w9L"

        fun encryptData(input: String): String? {
            return try {
                val c: Cipher = Cipher.getInstance(AES_MODE)
                c.init(Cipher.ENCRYPT_MODE, getSecretKey(), GCMParameterSpec(96, FIXED_IV.toByteArray()))
                val encodedBytes: ByteArray = c.doFinal(input.toByteArray())
                Base64.encodeToString(encodedBytes, Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e(TAG, "Encrypting text failed", e)
                null
            }
        }

        fun decryptData(encrypted: String): String? {
            return try {
                val c = Cipher.getInstance(AES_MODE)
                c.init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(96, FIXED_IV.toByteArray()))
                val decodedBytes = c.doFinal(Base64.decode(encrypted, Base64.DEFAULT))
                String(decodedBytes)
            } catch (e: Exception) {
                Log.e(TAG, "Decrypting text failed", e)
                null
            }
        }

        /**
         * Start-up operation only
         * Generate secret key and store in AndroidKeyStore to use to encrypt/decrypt data
         */
        fun generateSecretKey() {
            val keyStore = KeyStore.getInstance(AndroidKeyStore)
            keyStore.load(null)
            if (keyStore.containsAlias(KEY_ALIAS)) return

            val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                AndroidKeyStore
            )
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .build()
            )
            keyGenerator.generateKey()
        }

        private fun getSecretKey(): Key {
            return KeyStore.getInstance(AndroidKeyStore).also { it.load(null) }
                .getKey(KEY_ALIAS, null)
        }

    }
}