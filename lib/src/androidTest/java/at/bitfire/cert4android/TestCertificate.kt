package at.bitfire.cert4android

import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object TestCertificate {

    val certFactory = CertificateFactory.getInstance("X.509")

    val rawTestCert = "-----BEGIN CERTIFICATE-----\n" +
            "MIICxzCCAa+gAwIBAgIUe7x8TfMqQlJ+qTF/L+n6NqRqKAwwDQYJKoZIhvcNAQEL\n" +
            "BQAwDjEMMAoGA1UEAwwDdG50MB4XDTIwMDIxODE5NTYyMFoXDTMwMDIxNTE5NTYy\n" +
            "MFowDjEMMAoGA1UEAwwDdG50MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\n" +
            "AQEAzqBnVAWwIp8oOrcEJzplOLzd8ZKscmrJ0oxMf7oiYUE5+D3I1IjsCIeUdwfH\n" +
            "9zDKmNo3lwyFUTjw2WotssawmEH0LGfabi6h/1bbFG4QOQC7KYMtD8tqzR6F0WVe\n" +
            "oZ5uM5VvQB+dRvlq2CdWb8NcBkyd2pQ8RvJsft3fWY5ix3CL+OZ8lXOGqkxN780m\n" +
            "RkrxdYog3fvWC1CMSix8Y28q4JwRRAMP0JhBGIdFpnEPLowh6HhhPRiwAn+ksSey\n" +
            "Rz39AsUErUUCD7soCsDSzu80uieF9enEVweqnn/ayPhJlX0Drw7UwrC88UqdLqRD\n" +
            "Da/ucJYzKkMgHZJ7EXNh2WZpbwIDAQABox0wGzAJBgNVHRMEAjAAMA4GA1UdEQQH\n" +
            "MAWCA3RudDANBgkqhkiG9w0BAQsFAAOCAQEARpS40Z7hsIeiGqI4Pd33vIK69PwZ\n" +
            "yhfGwGT61Fo3CFyEBAc8y+93NkI3l6bd/En8UAkG87nE6qsBxc9LedFhabdcgsgf\n" +
            "rEN8vqNhhucuLyHPPzCi+C2sAJHgAGoKEgrfrmvdjCYUa1TJng59n5W5q8SmmYf/\n" +
            "AEokes8tF8DuZ3TOTXt2MwPFIbaZiDyn6J1+PYmEPMxoqbG5TDYkox4U4+zODDt0\n" +
            "0GFBlln9186eAbdKPSIkS8slAlB5ylBg/TlG/LmzQ/5ESqITyXSTJo5RSLCQ32iG\n" +
            "46rF2aPRcVr71DWqbV+YdwkI3N7EwZOIIEl6a9srF+q01LrIukWkScuU9Q==\n" +
            "-----END CERTIFICATE-----\n"
    /** some test certificate (untrusted Snakeoil certificate generated by Debian) */
    val testCert = certFactory.generateCertificate(rawTestCert.byteInputStream()) as X509Certificate

}