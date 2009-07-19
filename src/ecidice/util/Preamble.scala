package ecidice.util

/**
 * Contains useful implicit conversions.
 * <p>
 * Use <code>import Preamble._</code> to make them available in the surrounding
 * scope.
 */
object Preamble {
  implicit def floatToEvenRicherFloat(f: Float) = new EvenRicherFloat(f)
}
