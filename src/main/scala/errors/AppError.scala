package errors

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

  class DatabaseException(message: String = "", cause: Throwable = null)
    extends BreadException(ErrorCode.DataNotFoundError, message, cause)

  class VerboseServiceException(code: ErrorCode, message: String = "", cause: Throwable = null)
    extends BreadException(code, message, cause)
}