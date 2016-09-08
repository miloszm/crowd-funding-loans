package controllers

import javax.inject._

import play.api.data.validation.{Invalid, Valid}
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc._
import services.{LoanService, Validation}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class LoanController @Inject()(val loanService: LoanService)
                              (implicit exec: ExecutionContext) extends Controller with Validation {

  def createLoan = Action.async(parse.json) { implicit request =>
    Future {
      request.body.validate[LoanRequest] match {
        case JsSuccess(loanRequest, _) =>
          validate(loanRequest) match {
            case Valid =>
              val loadId = loanService.createLoan(loanRequest)
              Created(toJson(loadId))
            case Invalid(errors) =>
              UnprocessableEntity(toJson(errors.map(_.message)))
          }
        case je: JsError =>
          BadRequest(JsError.toJson(je))
      }
    }
  }

  def createOffer(loanId: String) = Action.async(parse.json) { implicit request =>
    Future {
      request.body.validate[OfferRequest] match {
        case JsSuccess(offerRequest, _) =>
          validate(offerRequest) match {
            case Valid =>
              loanService.createOffer(LoanId(loanId), offerRequest).fold(NotFound(s"LoanId $loanId not found")) {
                offerId => Created(toJson(offerId))
              }
            case Invalid(errors) =>
              UnprocessableEntity(toJson(errors.map(_.message)))
          }
        case je: JsError =>
          BadRequest(JsError.toJson(je))
      }
    }
  }

  def getCurrentOffer(loanId: String) = Action.async { implicit request =>
    Future {
      Try(loanService.getCurrentOffer(LoanId(loanId))) match {
        case Success(maybeOffer) => maybeOffer.fold(NotFound("No current offer")) { offer =>
          Ok(toJson(offer))
        }
        case Failure(e) => NotFound(s"LoanId $loanId not found")
      }
    }
  }
}
