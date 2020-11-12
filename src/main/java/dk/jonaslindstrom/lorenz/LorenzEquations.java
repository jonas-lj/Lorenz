package dk.jonaslindstrom.lorenz;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class LorenzEquations implements FirstOrderDifferentialEquations {

  public double σ, ρ, β;

  public LorenzEquations(double σ, double ρ, double β) {
    this.σ = σ;
    this.ρ = ρ;
    this.β = β;
  }

  public void computeDerivatives(double t, double[] y, double[] yDot) {
    yDot[0] = σ * (y[1] - y[0]);
    yDot[1] = y[0] * (ρ - y[2]) - y[1];
    yDot[2] = y[0] * y[1] - β * y[2];
  }

  public int getDimension() {
    return 3;
  }

}
