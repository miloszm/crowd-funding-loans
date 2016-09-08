import java.util.UUID
import java.util.UUID.fromString

import play.api.libs.json.Json

package object controllers {
  type OfferUUId = UUID
  type LoanUUId = UUID

  implicit def toUUID(id: String): UUID = fromString(id)

  implicit val loanRequestFormats = Json.format[LoanRequest]
  implicit val loanIdFormats = Json.format[LoanId]

  implicit val offerRequestFormats = Json.format[OfferRequest]
  implicit val offerIdFormats = Json.format[OfferId]
  implicit val currentOfferFormats = Json.format[CurrentOffer]

}
