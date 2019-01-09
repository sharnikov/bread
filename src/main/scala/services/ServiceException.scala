package services

class ServiceException(message: String = "", cause: Throwable = null) extends Exception(message, cause)

class DBException(message: String = "", cause: Throwable = null) extends Exception(message, cause)
