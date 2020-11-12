package dk.jonaslindstrom.lorenz;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;

public class Integrator implements FixedStepHandler {

  private final List<Vector3D> points = new ArrayList<>();
  private final ClassicalRungeKuttaIntegrator integrator;
  private final LorenzEquations equations;

  public Integrator(LorenzEquations equations, double delta, double step) {
    this.equations = equations;
    this.integrator = new ClassicalRungeKuttaIntegrator(delta);
    integrator.addStepHandler(new StepNormalizer(step, this));
  }

  /** Solve differential equation with the given starting point and store the resulting curve in
   * this.points. **/
  public List<Vector3D> integrate(double t, double[] init) {
    points.clear();
    integrator.integrate(equations, 0, init, t, new double[3]);
    return points;
  }

  @Override
  public void init(double v, double[] doubles, double v1) {

  }

  @Override
  public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
    this.points.add(new Vector3D(y));
  }

}