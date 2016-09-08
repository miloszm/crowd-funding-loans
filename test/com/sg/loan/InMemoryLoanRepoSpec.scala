package com.sg.loan

import java.util.UUID._

import controllers.{Loan, LoanId, LoanRequest, Offer, OfferId, OfferRequest}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import services.{IdGenerator, InMemoryLoanRepository, RandomIdGenerator}

class InMemoryLoanRepoSpec extends WordSpec with MustMatchers with MockitoSugar {

  "InMemoryRepository" should {
    "store the loan request and return the loan id" in {
      val idGenerator = mock[IdGenerator]
      val loanRepository = new InMemoryLoanRepository(idGenerator)

      val uuid = randomUUID()
      when(idGenerator.generateId()).thenReturn(uuid)
      val loanRequest = LoanRequest(1000.0, 100)
      val id = LoanId(uuid)

      loanRepository.storeLoan(loanRequest) must be (Loan(id, loanRequest))
    }

    "retrieve the loan" in {
      val idGenerator = mock[IdGenerator]
      val loanRepository = new InMemoryLoanRepository(idGenerator)

      val uuid = randomUUID()
      when(idGenerator.generateId()).thenReturn(uuid)
      val loanRequest = LoanRequest(1000.0, 100)
      val id = LoanId(uuid)

      val loan = loanRepository.storeLoan(loanRequest)
      loanRepository.getLoan(loan.loanId) must be (Some(Loan(id, loanRequest)))
    }

    "return None if the loan does not exist" in {
      val loanRepository = new InMemoryLoanRepository(new RandomIdGenerator)
      loanRepository.getLoan(LoanId(randomUUID())) must be (None)
    }

    "store the loan offer and return the offer" in {
      val idGenerator = mock[IdGenerator]
      val loanRepository = new InMemoryLoanRepository(idGenerator)

      val uuid = randomUUID()
      when(idGenerator.generateId()).thenReturn(uuid)
      val offer = OfferRequest(1000.0, 5.0)
      val loanId = LoanId(uuid)

      val offerId = OfferId(uuid)

      loanRepository.storeOffer(loanId, offer) must be (Offer(offerId, loanId, offer))
    }

    "retrieve the offer for a given loan" in {
      val idGenerator = mock[IdGenerator]
      val loanRepository = new InMemoryLoanRepository(idGenerator)

      val uuid = randomUUID()
      val uuid2 = randomUUID()

      when(idGenerator.generateId()).thenReturn(uuid).thenReturn(uuid2)
      val offerRequest = OfferRequest(1000.0, 5.0)
      val loanId = LoanId(uuid)

      val offerId = OfferId(uuid)
      val offer = Offer(offerId, loanId, offerRequest)
      val offer2 = Offer(OfferId(uuid2), loanId, offerRequest.copy(amount = 2))

      loanRepository.storeOffer(loanId, offerRequest)
      loanRepository.storeOffer(loanId, offerRequest.copy(amount = 2))

      loanRepository.getOffers(loanId) must contain theSameElementsAs List(offer, offer2)
    }
  }
}
