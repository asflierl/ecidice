package object ecidice {
  def init[A](a: A)(f: A => Any) = { f(a); a }
}