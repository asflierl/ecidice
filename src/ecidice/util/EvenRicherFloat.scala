package ecidice.util

/**
 * Decorates a floating point number, providing useful non-primitive operations.
 * 
 * @author Andreas Flierl
 */
class EvenRicherFloat(f: Float) {
  /**
   * Enables the syntax <code>2f to 4f step .1f</code> to generate a stream 
   * "range" for floats. The lower and upper bounds are always included.
   * 
   * @param max the upper bound for the returned stream range
   * @return a "continuous" stream from <code>f</code> to <code>max</code>; its 
   *         <code>step</code> method needs to be called to turn it into a 
   *         discrete stream (as known from the std. library)
   */
  def to(max: Float) = {
    object ContinuousStream {
      def step(by: Float) = {
        def seq(now: Float) : Stream[Float] = 
          if (now > max) Stream.cons(max, Stream.empty) 
          else Stream.cons(now, seq(now + by))
        
        seq(f)
      }
    }
    ContinuousStream
  }
}
