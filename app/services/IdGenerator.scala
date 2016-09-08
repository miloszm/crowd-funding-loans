package services

import java.util.UUID
import java.util.UUID._
import javax.inject.Singleton

trait IdGenerator {
  def generateId(): UUID
}

@Singleton
class RandomIdGenerator extends IdGenerator {
  override def generateId(): UUID = randomUUID()
}
