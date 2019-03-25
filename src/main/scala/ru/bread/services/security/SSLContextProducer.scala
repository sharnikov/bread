package ru.bread.services.security

import java.io.{FileInputStream, IOException, InputStream}
import java.security.{KeyStore, SecureRandom}

import akka.http.scaladsl.UseHttp2.Negotiated
import akka.http.scaladsl.{ConnectionContext, HttpsConnectionContext}
import akka.stream.TLSClientAuth.Want
import com.typesafe.scalalogging.LazyLogging
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import ru.bread.settings.config.Settings

import scala.util.Try

trait SSLContextProducer {
  def getConnectionContext(): ConnectionContext
}

class SimpleSSLContextProducer(settings: Settings) extends SSLContextProducer with LazyLogging {

  override def getConnectionContext(): ConnectionContext = {

    val sslSettings = settings.sslSettings()
    val keyStorageSettings = settings.sslSettings().keyStorage

    val password = keyStorageSettings.password.toCharArray
    val keystorePath = keyStorageSettings.path

    val keystore = KeyStore.getInstance(keyStorageSettings.storageType)

    val keystoreStream = new FileInputStream(keystorePath)

    wrapStream(keystoreStream, keystore.load(_, password))

    val keyManagerFactory = KeyManagerFactory.getInstance(sslSettings.keyManagerAlgorithm)
    keyManagerFactory.init(keystore, password)

    val trustManager = TrustManagerFactory.getInstance(sslSettings.trustManagerAlgorithm)
    trustManager.init(keystore)
    trustManager.getTrustManagers

    val sslContext = SSLContext.getInstance(sslSettings.sslAlgorithm)
    sslContext.init(keyManagerFactory.getKeyManagers, trustManager.getTrustManagers, new SecureRandom)

    val connectionContext: ConnectionContext = new HttpsConnectionContext(
      sslContext = sslContext,
      sslConfig = None,
      enabledCipherSuites = None,
      enabledProtocols = None,
      clientAuth = Some(Want),
      sslParameters = None,
      http2 = Negotiated
    )

    connectionContext
  }

  private def wrapStream(stream: InputStream, f: InputStream => Unit) =
    try {
      f(stream)
    } finally {
      Try(
        if (stream != null) {
          stream.close()
        }
      ).recover {
        case io: IOException => logger.warn("Keystore stream wasn't close properly", io)
        case ex: Throwable => logger.warn("An exception occurred during closing of the stream", ex)
      }
    }
}
