package dk.jonaslindstrom.lorenz;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

@FunctionalInterface
public interface PointProcessor<S> {

  S apply(Vector3D point, S s, int i);

}
