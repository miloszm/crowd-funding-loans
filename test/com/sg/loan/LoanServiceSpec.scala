package com.sg.loan

import java.util.UUID.randomUUID

import controllers.{CurrentOffer, Loan, LoanId, LoanRequest, Offer, OfferId, OfferRequest}
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import services.{InMemoryLoanRepository, LoanService, RandomIdGenerator}

class LoanServiceSpec extends WordSpec with MustMatchers with MockitoSugar {

  "LoanService" should {
    "store the loan request and return the loan id" in {
      val loanRepository = mock[InMemoryLoanRepository]
      val loanService = new LoanService(loanRepository)

      val loanRequest = LoanRequest(1000.0, 100)
      val id = LoanId(randomUUID())
      when(loanRepository.storeLoan(loanRequest)).thenReturn(Loan(id, loanRequest.amount, loanRequest.durationInDays))

      loanService.createLoan(loanRequest) must be (id)
    }
  }

  "LoanService" should {
    "store the offer request and return the offer id" in {
      val loanRepository = mock[InMemoryLoanRepository]
      val loanService = new LoanService(loanRepository)

      val offerRequest = OfferRequest(1000.0, 5)
      val loanId = LoanId(randomUUID())
      val offerId = OfferId(randomUUID())
      when(loanRepository.getLoan(loanId)).thenReturn(Some(Loan(loanId, 100, 5)))
      when(loanRepository.storeOffer(loanId, offerRequest)).thenReturn(Offer(offerId,loanId, offerRequest))

      loanService.createOffer(loanId, offerRequest) must be (Some(offerId))
    }
  }

  "LoanService" should {
    "calculate current offer with more offers than requested amount" in {
      val repository = new InMemoryLoanRepository(new RandomIdGenerator)
      val loan = repository.storeLoan(LoanRequest(1000.0, 100))
      repository.storeOffer(loan.loanId, OfferRequest(100, 5))
      repository.storeOffer(loan.loanId, OfferRequest(600, 6))
      repository.storeOffer(loan.loanId, OfferRequest(600, 7))
      repository.storeOffer(loan.loanId, OfferRequest(500, 8.2))

      val loanService = new LoanService(repository)

      loanService.getCurrentOffer(loan.loanId) must be (Some(CurrentOffer(1000.0, 6.2)))
    }
  }

  "LoanService" should {
    "calculate current offer with offers amount less than requested amount" in {
      val repository = new InMemoryLoanRepository(new RandomIdGenerator)
      val loan = repository.storeLoan(LoanRequest(1000.0, 100))
      repository.storeOffer(loan.loanId, OfferRequest(100, 5))
      repository.storeOffer(loan.loanId, OfferRequest(500, 8.6))

      val loanService = new LoanService(repository)

      loanService.getCurrentOffer(loan.loanId) must be (Some(CurrentOffer(600.0, 8)))
    }
  }
}
