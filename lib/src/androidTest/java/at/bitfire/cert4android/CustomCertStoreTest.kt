/***************************************************************************************************
 * Copyright © All Contributors. See LICENSE and AUTHORS in the root directory for details.
 **************************************************************************************************/

package at.bitfire.cert4android

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class CustomCertStoreTest {

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val certStore = CustomCertStore.getInstance(context)

    val testCert = TestCertificate.testCert


    @Before
    fun clearKeys() {
        certStore.clearUserDecisions()
        assertFalse(certStore.userKeyStore.aliases().hasMoreElements())
    }


    @Test
    fun testSetTrustedByUser() {
        // set it to untrustd before to test whether setTrustedByUser removes the cert from untrustedCerts, too
        certStore.setUntrustedByUser(testCert)
        assertEquals(testCert, certStore.untrustedCerts.first())

        // set to trusted, should save to disk
        certStore.setTrustedByUser(testCert)

        assertTrue(certStore.isTrustedByUser(testCert))
        assertTrue(certStore.untrustedCerts.isEmpty())

        // test whether cert was stored to disk:
        // create another cert store to make sure data is loaded from disk again
        val anotherCertStore = CustomCertStore(context)
        assertEquals("4694b8d19ee1b087a21aa2383dddf7bc82baf4fc19ca17c6c064fad45a37085c8404073ccbef7736423797a6ddfc49fc500906f3b9c4eaab01c5cf4b79d16169b75c82c81fac437cbea36186e72e2f21cf3f30a2f82dac0091e0006a0a120adfae6bdd8c26146b54c99e0e7d9f95b9abc4a69987ff004a247acf2d17c0ee6774ce4d7b763303c521b699883ca7e89d7e3d89843ccc68a9b1b94c3624a31e14e3ecce0c3b74d061419659fdd7ce9e01b74a3d22244bcb25025079ca5060fd3946fcb9b343fe444aa213c97493268e5148b090df6886e3aac5d9a3d1715afbd435aa6d5f98770908dcdec4c1938820497a6bdb2b17eab4d4bac8ba45a449cb94f5",
            anotherCertStore.userKeyStore.aliases().nextElement())
    }

    @Test
    fun testSetUnTrustedByUser() {
        // set to trusted before to test whether setUntrustedByUser removes the cert from trusted key store, too
        certStore.setTrustedByUser(testCert)    // saves trust to disk

        certStore.setUntrustedByUser(testCert)
        assertEquals(testCert, certStore.untrustedCerts.first())

        // test whether now empts key store was saved to disk:
        // create another cert store to make sure data is loaded from disk again
        val anotherCertStore = CustomCertStore(context)
        assertFalse(anotherCertStore.isTrustedByUser(testCert))
    }


}