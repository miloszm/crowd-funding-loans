package com.sg.loan

import java.util.UUID._

import controllers.{CurrentOffer, LoanController, LoanId, LoanRequest, OfferId, OfferRequest}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import play.api.libs.json.Json.toJson
import play.api.test.Helpers._
import play.api.test._
import services.LoanService

import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.Mockito._

class LoanControllerSpec extends PlaySpec with OneAppPerTest with MockitoSugar with SpecSupport {

  "LoanController" should {
    "should create a loan request and return 201" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val loadRequest = LoanRequest(1000.00, 100)

      when(loanService.createLoan(loadRequest)).thenReturn(LoanId(randomUUID()))
      val request = FakeRequest().withBody(toJson(loadRequest))
      val response = controller.createLoan()(request)
      status(response) mustBe CREATED
    }
  }

  "LoanController" should {
    "should create an offer request and return 201" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val offerRequest = OfferRequest(1000.00, 5)
      val loanId = randomUUID()

      when(loanService.createOffer(LoanId(loanId), offerRequest)).thenReturn(Some(OfferId(randomUUID())))

      val request = FakeRequest().withBody(toJson(offerRequest))
      val response = controller.createOffer(loanId.toString)(request)
      status(response) mustBe CREATED
    }
  }

  "LoanController" should {
    "should get the current offer and return 200" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val loanId = randomUUID()
      when(loanService.getCurrentOffer(LoanId(loanId))).thenReturn(Some(CurrentOffer(100.0, 10.0)))
      val request = FakeRequest().withHeaders(("Content-Type", "application/json"))
      val response = controller.getCurrentOffer(loanId.toString)(request)
      status(response) mustBe OK
    }
  }

  "LoanController" should {
    "should return 404 if no current offers available" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val loanId = randomUUID()
      val request = FakeRequest().withHeaders(("Content-Type", "application/json"))

      when(loanService.getCurrentOffer(LoanId(loanId))).thenReturn(None)
      val response = controller.getCurrentOffer(loanId.toString)(request)
      status(response) mustBe NOT_FOUND
    }
  }


  "LoanController" should {
    "should return 404 when getting the current offer for a non existing loan" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val loanId = randomUUID()
      val request = FakeRequest().withHeaders(("Content-Type", "application/json"))

      when(loanService.getCurrentOffer(LoanId(loanId))).thenThrow(new RuntimeException())
      val response = controller.getCurrentOffer(loanId.toString)(request)
      status(response) mustBe NOT_FOUND
    }
  }

  "LoanController" should {
    "should return 422 when creating a loan with invalid information" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val loadRequest = LoanRequest(-10, 100)

      val request = FakeRequest().withBody(toJson(loadRequest))
      val response = controller.createLoan()(request)
      status(response) mustBe UNPROCESSABLE_ENTITY
    }
  }

  "LoanController" should {
    "should return 422 when creating offer with invalid information" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val offerRequest = OfferRequest(-10.00, 5)
      val loanId = randomUUID()

      val request = FakeRequest().withBody(toJson(offerRequest))
      val response = controller.createOffer(loanId.toString)(request)
      status(response) mustBe UNPROCESSABLE_ENTITY
    }
  }

  "LoanController" should {
    "should return 404 if creating an offer for a non existing loan" in {
      val loanService =  mock[LoanService]
      val controller = new LoanController(loanService)

      val offerRequest = OfferRequest(1000.00, 5)
      val loanId = randomUUID()

      when(loanService.createOffer(LoanId(loanId), offerRequest)).thenReturn(None)

      val request = FakeRequest().withBody(toJson(offerRequest))
      val response = controller.createOffer(loanId.toString)(request)
      status(response) mustBe NOT_FOUND
    }
  }
}
