package com.sg.loan

import controllers.{CurrentOffer, LoanId, LoanRequest, OfferRequest}
import play.api.libs.json.Json

trait SpecSupport {
  implicit val loanRequestFormats = Json.format[LoanRequest]
  implicit val offerRequestFormats = Json.format[OfferRequest]
  implicit val currentOfferFormats = Json.format[CurrentOffer]

  implicit class LoanIdJsonString(json: String) {
    def toLoanId = Json.parse(json).as[LoanId]
  }

  implicit class CurrentOfferJsonString(json: String) {
    def toCurrentOffer = Json.parse(json).as[CurrentOffer]
  }

}
