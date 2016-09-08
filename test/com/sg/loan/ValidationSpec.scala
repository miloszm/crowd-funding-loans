package com.sg.loan

import controllers.{LoanRequest, OfferRequest}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.data.validation.{Invalid, Valid, ValidationError}
import services.Validation

class ValidationSpec extends WordSpec with MustMatchers with Validation {

  "Validation" should {
    "return a valid response when creating a valid loan" in {
      validate(LoanRequest(10, 3)) mustBe Valid
    }
  }

  "Validation" should {
    "return an invalid response when creating a loan with negative amount" in {
      validate(LoanRequest(-10, 3)) mustBe Invalid(Seq(ValidationError("Invalid amount")))
    }
  }

  "Validation" should {
    "return an invalid response when creating a loan with negative duration" in {
      validate(LoanRequest(10, -3)) mustBe Invalid(Seq(ValidationError("Invalid duration")))
    }
  }

  "Validation" should {
    "return a valid response when creating a valid offer" in {
      validate(OfferRequest(10, 3)) mustBe Valid
    }
  }

  "Validation" should {
    "return an invalid response when creating an offer with negative amount" in {
      validate(OfferRequest(-10, 3)) mustBe Invalid(Seq(ValidationError("Invalid amount")))
    }
  }
}
