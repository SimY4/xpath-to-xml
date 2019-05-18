package com.github.simy4.xpath.scala.compat

private[scala] object Converters {

  import scala.jdk.CollectionConverters._

  implicit def asJavaIterable[A](it: Iterable[A]): java.lang.Iterable[A] = it.asJava

}
