import core.LikeZIO
import org.scalatest._

object LikeZIOSpec {
  def f(x: Int): LikeZIO[Int] = LikeZIO(x * x)

  def g(x: Int): LikeZIO[Int] = LikeZIO(x + 1)

  def leftUnitLaw(): Boolean = {
    //unit(x) flatMap f == f(x)
    val x = 5
    val monad = LikeZIO(x)
    monad.flatMap(f) == f(x)

  }

  def rightUnitLaw(): Boolean = {
    //monad flatMap unit == monad
    val x = 5
    val monad: LikeZIO[Int] = LikeZIO(x)
    monad.flatMap(x => LikeZIO(x)) == monad
  }

  def associativityLaw(): Boolean = {
    //(monad flatMap f) flatMap g == monad flatMap(x => f(x) flatMap g)
    val x = 5
    val monad: LikeZIO[Int] = LikeZIO(x)
    val left = monad flatMap f flatMap g
    val right = monad flatMap (x => f(x) flatMap g)
    left == right
  }
}

class LikeZIOSpec extends FlatSpec with Matchers {

  import LikeZIOSpec._

  it should "Check equals && hashCode" in {
    val leftGood = LikeZIO(10)
    val rightGood = LikeZIO(10)
    val rightBadValue = LikeZIO(11)
    val rightBadType = LikeZIO("10")

    leftGood shouldEqual rightGood
    leftGood.hashCode shouldEqual rightGood.hashCode

    leftGood should not equal rightBadValue
    leftGood.hashCode should not equal rightBadValue.hashCode

    leftGood should not equal rightBadType
    leftGood.hashCode should not equal rightBadType.hashCode
  }

  it should "LikeZIO is monada" in {
    leftUnitLaw() shouldEqual true
    rightUnitLaw() shouldEqual true
    associativityLaw() shouldEqual true
  }
}
