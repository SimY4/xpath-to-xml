package com.github.simy4.xpath.scala.compat

private[scala] object Converters {

  import collection.JavaConverters._

  implicit def asJavaIterable[A](it: Iterable[A]): java.lang.Iterable[A] = it.asJava

}
