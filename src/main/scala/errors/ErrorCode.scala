package errors

sealed trait ErrorCode

object ErrorCode {
  case object InternalError extends ErrorCode
  case object DataNotFound extends ErrorCode
  case object OperationRejected extends ErrorCode
  case object ParsingError extends ErrorCode
}
