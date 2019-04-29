package ru.bread.errors

trait AppError {
  def code: ErrorCode
  def message: String
}

object AppError {

  class BreadException(val code: ErrorCode, val message: String, cause: Throwable = null)
    extends Exception(message, cause) with AppError

  class ServiceException(message: String = "", cause: Throwable = null) extends
    BreadException(ErrorCode.InternalError, message, cause)

  class ParseFailedException(message: String = "", cause: Throwable = null) extends
    BreadException(ErrorCode.ParsingError, message, cause)

  class DatabaseDataNotFoundException(message: String = "", cause: Throwable = null)
    extends BreadException(ErrorCode.DataNotFoundError, message, cause)

  class VerboseServiceException(code: ErrorCode, message: String = "", cause: Throwable = null)
    extends BreadException(code, message, cause)

  class AuthorizationException(message: String = "", cause: Throwable = null)
    extends VerboseServiceException(ErrorCode.AuthorizationError, message, cause)

  class ValidationException(message: String = "", cause: Throwable = null) extends
    VerboseServiceException(ErrorCode.ValidationError, message, cause)
}