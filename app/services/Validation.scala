package services

import controllers.{LoanRequest, OfferRequest}
import play.api.data.validation.{Invalid, Valid, ValidationError, ValidationResult}

trait Validation {

  def validate(loanRequest: LoanRequest): ValidationResult = {
    val amountErrors = if (loanRequest.amount <= 0) Seq(ValidationError("Invalid amount")) else Seq()
    val daysErrors = if (loanRequest.durationInDays <= 0) Seq(ValidationError("Invalid duration")) else Seq()
    val errors = amountErrors ++ daysErrors
    if (errors.isEmpty)
      Valid
    else
      Invalid(errors)
  }

  def validate(offerRequest: OfferRequest): ValidationResult = {
    if (offerRequest.amount <= 0)
      Invalid(Seq(ValidationError("Invalid amount")))
    else Valid
  }

}
