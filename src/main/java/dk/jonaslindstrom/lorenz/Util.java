package dk.jonaslindstrom.lorenz;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Region.Location;

public class Util {

  /** Map linearly from the interval [a,b] to [c,d] */
  static double mapToRange(double x, Interval domain, Interval image) {
    assert !domain.checkPoint(x, Double.MIN_VALUE).equals(Location.OUTSIDE);
    double x̄ = (x - domain.getInf()) / domain.getSize();
    return image.getInf() + x̄ * image.getSize();
  }

  /** Compute the range of a curve defined by the given points. */
  static List<Interval> boundingBox(List<Vector3D> curve) {
    List<Interval> ranges = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      int finalI = i;
      double min = curve.stream().mapToDouble(x -> x.toArray()[finalI]).min().getAsDouble();
      double max = curve.stream().mapToDouble(x -> x.toArray()[finalI]).max().getAsDouble();
      ranges.add(new Interval(min, max));
    }
    return ranges;
  }
}
