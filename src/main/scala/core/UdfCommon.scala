package core

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import scala.reflect.runtime.universe.TypeTag

sealed trait UdfCommon {
//  def scalaUdf: UserDefinedFunction
}

object UdfCommon {

  trait Udf0[RT] extends UdfCommon {
    protected def udfFun: Function0[LikeZIO[RT]]

    def javaUdf(): LikeZIO.LikeZIOForSpark[RT] = {
      udfFun.apply().prepareForSpark
    }

//    override def scalaUdf: UserDefinedFunction = udf {
//      () => this.udfFun.apply().prepareForSpark
//    }
  }

  trait Udf1[RT, A1] extends UdfCommon {
    protected def udfFun: Function1[A1, LikeZIO[RT]]

    def javaUdf(a1: A1): LikeZIO.LikeZIOForSpark[RT] = {
      udfFun.apply(a1).prepareForSpark
    }

//    override def scalaUdf: UserDefinedFunction = udf {
//      a1: A1 => udfFun.apply(a1).prepareForSpark
//    }
  }

  trait Udf2[RT, A1, A2] extends UdfCommon {
    protected def udfFun: Function2[A1, A2, LikeZIO[RT]]

    def javaUdf(a1: A1, a2: A2): LikeZIO.LikeZIOForSpark[RT] = {
      udfFun.apply(a1, a2).prepareForSpark
    }

//    override def scalaUdf: UserDefinedFunction = udf {
//      (a1: A1, a2: A2) => udfFun.apply(a1, a2).prepareForSpark
//    }
  }

  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag](f: Function3[A1, A2, A3, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag](f: Function4[A1, A2, A3, A4, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag](f: Function5[A1, A2, A3, A4, A5, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag](f: Function6[A1, A2, A3, A4, A5, A6, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag](f: Function7[A1, A2, A3, A4, A5, A6, A7, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag, A8: TypeTag](f: Function8[A1, A2, A3, A4, A5, A6, A7, A8, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag, A8: TypeTag, A9: TypeTag](f: Function9[A1, A2, A3, A4, A5, A6, A7, A8, A9, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag, A8: TypeTag, A9: TypeTag, A10: TypeTag](f: Function10[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }


  //  import scala.reflect.runtime.universe._
  //  def udf[T: TypeTag](f: Function0[LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.apply())
  //
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag](f: Function1[A1, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag](f: Function2[A1, A2, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.)
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag](f: Function3[A1, A2, A3, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag](f: Function4[A1, A2, A3, A4, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag](f: Function5[A1, A2, A3, A4, A5, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag](f: Function6[A1, A2, A3, A4, A5, A6, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag](f: Function7[A1, A2, A3, A4, A5, A6, A7, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag, A8: TypeTag](f: Function8[A1, A2, A3, A4, A5, A6, A7, A8, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag, A8: TypeTag, A9: TypeTag](f: Function9[A1, A2, A3, A4, A5, A6, A7, A8, A9, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }
  //
  //
  //  def udf[T: TypeTag, A1: TypeTag, A2: TypeTag, A3: TypeTag, A4: TypeTag, A5: TypeTag, A6: TypeTag, A7: TypeTag, A8: TypeTag, A9: TypeTag, A10: TypeTag](f: Function10[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, LikeZIO[T]]): UserDefinedFunction = {
  //    functions.udf(f.andThen(_.prepareForSpark))
  //  }

}
