package services

class ServiceException(message: String = "", cause: Throwable = null) extends Exception(message, cause)
