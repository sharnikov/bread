package ru.bread.services.internal

import org.apache.commons.codec.digest.DigestUtils
import ru.bread.settings.config.Settings

trait EncryptService {
  def encrypt(data: String): String
}

class MD5EncryptService(settings: Settings) extends EncryptService {

  override def encrypt(data: String): String = {
    DigestUtils.md5Hex(s"$data${settings.commonSettings().salt()}")
  }
}
