package errors

sealed trait ErrorCode

object ErrorCode {
  case object InternalError extends ErrorCode
  case object DataNotFoundError extends ErrorCode
  case object AuthorizationError extends ErrorCode
  case object ParsingError extends ErrorCode
}
