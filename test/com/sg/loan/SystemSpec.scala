package com.sg.loan

import java.util.UUID._

import controllers.{LoanRequest, OfferRequest}
import org.scalatestplus.play._
import play.api.libs.json.Json.toJson
import play.api.test.Helpers._
import play.api.test._

class SystemSpec extends PlaySpec with OneAppPerTest with SpecSupport {

  "Creating a loan request" should {
    "return 201" in {
      val loanRequest = LoanRequest(1000.00, 100)
      val request = FakeRequest(POST, "/loans").withBody(toJson(loanRequest))
      val response = route(app, request).get
      status(response) mustBe CREATED
    }
  }

  "Creating an offer" should {
    "return 201" in {
      //Given a loan
      val loanRequest = FakeRequest(POST, "/loans").withBody(toJson(LoanRequest(1000.00, 100)))
      val loanResponse = route(app, loanRequest).get
      val loanId = contentAsString(loanResponse).toLoanId

      // When creating an offer
      val offerRequest = OfferRequest(1000.00, 5)
      val request = FakeRequest(POST, s"/loans/${loanId.loanId.toString}/offers").withBody(toJson(offerRequest))
      val response = route(app, request).get

      // Then the offer should be created
      status(response) mustBe CREATED
    }
  }

  "Getting current offer for unknown loan" should {
    "should return 404" in {
      val loanId = randomUUID().toString
      val request = FakeRequest(GET, s"/loans/$loanId/offers/current")
        .withHeaders(("Content-Type", "application/json"))
      val response = route(app, request).get
      status(response) mustBe NOT_FOUND
    }
  }

  "Getting the current offer" should {
    "return the offer" in {
      //Given a loan
      val loanRequest = FakeRequest(POST, "/loans").withBody(toJson(LoanRequest(1000.00, 100)))
      val loanResponse = route(app, loanRequest).get
      val loanId = contentAsString(loanResponse).toLoanId

      // And some offers
      val offer1 = FakeRequest(POST, s"/loans/${loanId.loanId.toString}/offers").withBody(toJson(OfferRequest(100.0, 5.0)))
      route(app, offer1).get

      val offer2 = FakeRequest(POST, s"/loans/${loanId.loanId.toString}/offers").withBody(toJson(OfferRequest(500.0, 8.6)))
      route(app, offer2).get

      // When getting the current offer
      val currentOfferRequest = FakeRequest(GET, s"/loans/${loanId.loanId.toString}/offers/current")
        .withHeaders(("Content-Type", "application/json"))
      val response = route(app, currentOfferRequest).get

      // Then the current offer should be calculated
      status(response) mustBe OK
      val currentOffer = contentAsString(response).toCurrentOffer
      currentOffer.combinedInterest mustBe 8
      currentOffer.amount mustBe 600
    }
  }
}
