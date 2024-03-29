/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

package at.bitfire.cert4android

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.conscrypt.Conscrypt
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.Security
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.logging.Level
import javax.net.ssl.SSLContext

class CustomCertStore internal constructor(
    private val context: Context,
    private val userTimeout: Long = 60000L
) {

    companion object {

        const val KEYSTORE_DIR = "KeyStore"
        const val KEYSTORE_NAME = "KeyStore.bks"

        @SuppressLint("StaticFieldLeak")
        internal var instance: CustomCertStore? = null

        init {
            // initialize Conscrypt
            Security.insertProviderAt(Conscrypt.newProvider(), 1)

            val version = Conscrypt.version()
            Cert4Android.log.info("Using Conscrypt/${version.major()}.${version.minor()}.${version.patch()} for TLS")
            val engine = SSLContext.getDefault().createSSLEngine()
            Cert4Android.log.info("Enabled protocols: ${engine.enabledProtocols.joinToString(", ")}")
            Cert4Android.log.info("Enabled ciphers: ${engine.enabledCipherSuites.joinToString(", ")}")
        }

        @Synchronized
        fun getInstance(context: Context): CustomCertStore {
            instance?.let {
                return it
            }

            val newInstance = CustomCertStore(context.applicationContext)
            instance = newInstance
            return newInstance
        }

    }

    /** system default TrustStore */
    private val systemKeyStore by lazy { Conscrypt.getDefaultX509TrustManager() }

    /** custom TrustStore */
    private val userKeyStoreFile = File(context.getDir(KEYSTORE_DIR, Context.MODE_PRIVATE), KEYSTORE_NAME)
    internal val userKeyStore = KeyStore.getInstance(KeyStore.getDefaultType())!!

    /** in-memory store for untrusted certs */
    internal var untrustedCerts = HashSet<X509Certificate>()

    init {
        loadUserKeyStore()
    }

    @Synchronized
    fun clearUserDecisions() {
        Cert4Android.log.info("Clearing user-(dis)trusted certificates")

        for (alias in userKeyStore.aliases())
            userKeyStore.deleteEntry(alias)
        saveUserKeyStore()

        // clear untrusted certs
        untrustedCerts.clear()
    }

    /**
     * Determines whether a certificate chain is trusted.
     */
    fun isTrusted(chain: Array<X509Certificate>, authType: String, trustSystemCerts: Boolean, appInForeground: StateFlow<Boolean>?): Boolean {
        if (chain.isEmpty())
            throw IllegalArgumentException("Certificate chain must not be empty")
        val cert = chain[0]

        synchronized(this) {
            if (isTrustedByUser(cert))
                // explicitly accepted by user
                return true

            // explicitly rejected by user
            if (untrustedCerts.contains(cert))
                return false

            // check system certs, if applicable
            if (trustSystemCerts)
                try {
                    systemKeyStore.checkServerTrusted(chain, authType)

                    // trusted by system
                    return true
                } catch (ignored: CertificateException) {
                    // not trusted by system, ask user
                }
        }

        if (appInForeground == null) {
            Cert4Android.log.log(Level.INFO, "Certificate not known and running in non-interactive mode, rejecting")
            return false
        }

        return runBlocking {
            val ui = UserDecisionRegistry.getInstance(context)

            try {
                withTimeout(userTimeout) {
                    ui.check(cert, appInForeground.value)
                }
            } catch (e: TimeoutCancellationException) {
                Cert4Android.log.log(Level.WARNING, "User timeout while waiting for certificate decision, rejecting")
                false
            }
        }
    }

    /**
     * Determines whether a certificate has been explicitly accepted by the user. In this case,
     * we can ignore an invalid host name for that certificate.
     */
    @Synchronized
    fun isTrustedByUser(cert: X509Certificate): Boolean =
        userKeyStore.getCertificateAlias(cert) != null

    @Synchronized
    fun setTrustedByUser(cert: X509Certificate) {
        Cert4Android.log.info("Trusted by user: $cert")

        userKeyStore.setCertificateEntry(CertUtils.getTag(cert), cert)
        saveUserKeyStore()

        untrustedCerts -= cert
    }

    @Synchronized
    fun setUntrustedByUser(cert: X509Certificate) {
        Cert4Android.log.info("Distrusted by user: $cert")

        userKeyStore.deleteEntry(CertUtils.getTag(cert))
        saveUserKeyStore()

        untrustedCerts += cert
    }


    @Synchronized
    private fun loadUserKeyStore() {
        try {
            FileInputStream(userKeyStoreFile).use {
                userKeyStore.load(it, null)
                Cert4Android.log.fine("Loaded ${userKeyStore.size()} trusted certificate(s)")
            }
        } catch(e: Exception) {
            Cert4Android.log.fine("No key store for trusted certificates (yet); creating in-memory key store.")
            try {
                userKeyStore.load(null, null)
            } catch(e: Exception) {
                Cert4Android.log.log(Level.SEVERE, "Couldn't initialize in-memory key store", e)
            }
        }
    }

    @Synchronized
    private fun saveUserKeyStore() {
        try {
            FileOutputStream(userKeyStoreFile).use { userKeyStore.store(it, null) }
        } catch(e: Exception) {
            Cert4Android.log.log(Level.SEVERE, "Couldn't save custom certificate key store", e)
        }
    }

}