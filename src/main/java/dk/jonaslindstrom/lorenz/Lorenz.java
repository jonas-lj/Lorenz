package dk.jonaslindstrom.lorenz;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Lorenz {

  /**
   * Compute a curve on a given Lorenz system and process a discrete subset of the points.
   *
   * @param init        Initial point.
   * @param equations   The ODE defining the Lorenz system.
   * @param t1          We start at t_0 = 0 and end at t_1.
   * @param processor   Function consuming points sequentially. May keep a state of type State.
   * @param boundingBox Before processing the points, the minimum bounding box of the curve is
   *                    computed.
   * @param s0          Initial state.
   * @param done        Returns true when we should finish processing. Otherwise it ends at t = t1.
   * @param <State>     The type of state.
   */
  public static <State> void run(Vector3D init,
      LorenzEquations equations, double t1, PointProcessor<State> processor,
      Consumer<List<Interval>> boundingBox, State s0, Predicate<State> done, double delta,
      double step) {

    Integrator integrator = new Integrator(equations, delta, step);

    List<Vector3D> curve = integrator.integrate(t1, init.toArray());
    boundingBox.accept(Util.boundingBox(curve));
    State s = s0;

    for (int i = 0; i < curve.size(); i++) {
      s = processor.apply(curve.get(i), s, i);
      if (done.test(s)) {
        break;
      }
    }
  }

  public static <State> void run(Vector3D init,
      LorenzEquations equations, double t1, PointProcessor<State> processor,
      Consumer<List<Interval>> boundingBox, State s0, Predicate<State> done) {
    run(init, equations, t1, processor, boundingBox, s0, done, 0.001, 0.01);
  }
}
