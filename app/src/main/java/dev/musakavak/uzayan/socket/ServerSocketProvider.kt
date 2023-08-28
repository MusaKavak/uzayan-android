package dev.musakavak.uzayan.socket

import android.annotation.SuppressLint
import org.bouncycastle.asn1.x500.X500NameBuilder
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


fun getSimpleServerSocket(): ServerSocket {
    return ServerSocket().also {
        it.bind(InetSocketAddress(0))
    }
}

fun getSSLServerSocket(): ServerSocket {
    return getSSLContext().serverSocketFactory.createServerSocket().also {
        it.bind(InetSocketAddress(0))
    }
}

private fun getSSLContext(): SSLContext {
    val sslContext = SSLContext.getInstance("TLSv1.3")
    sslContext.init(
        createKeyManagers(),
        createTrustManager(),
        SecureRandom()
    )
    return sslContext
}

private fun createKeyManagers(): Array<KeyManager> {
    val nameBuilder = nameBuilder()
    val validDate = validDate()
    val keys = keys()

    val certBuilder = JcaX509v3CertificateBuilder(
        nameBuilder.build(),
        BigInteger.ONE,
        validDate.first,
        validDate.second,
        nameBuilder.build(),
        keys.first
    )

    val contentSigner = JcaContentSignerBuilder("SHA256WithRSA").build(keys.second)
    val certificateBytes = certBuilder.build(contentSigner).encoded

    val certificate: Certificate =
        CertificateFactory.getInstance("X.509")
            .generateCertificate(ByteArrayInputStream(certificateBytes))

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)
    keyStore.setKeyEntry("key", keys.second, "".toCharArray(), arrayOf(certificate))

    val keyManagerFactory =
        KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    keyManagerFactory.init(keyStore, "".toCharArray())
    return keyManagerFactory.keyManagers
}

private fun createTrustManager(): Array<TrustManager> {
    return arrayOf(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                certs: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                certs: Array<X509Certificate?>?,
                authType: String?
            ) {
            }
        })
}

private fun nameBuilder(): X500NameBuilder {
    val nameBuilder = X500NameBuilder(BCStyle.INSTANCE)
    nameBuilder.addRDN(BCStyle.CN, "AndroidDevice")
    nameBuilder.addRDN(BCStyle.OU, "Uzayan")
    return nameBuilder
}

private fun validDate(): Pair<Date, Date> {
    val localDate = LocalDate.now().minusYears(1)
    val notBefore = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
    val notAfter = localDate.plusYears(10).atStartOfDay(ZoneId.systemDefault()).toInstant()
    return Pair(Date.from(notBefore), Date.from(notAfter))
}

private fun keys(): Pair<PublicKey, PrivateKey> {
    val kpg = KeyPairGenerator.getInstance("RSA")
    kpg.initialize(2048)
    val kp = kpg.genKeyPair()
    return Pair(kp.public, kp.private)
}