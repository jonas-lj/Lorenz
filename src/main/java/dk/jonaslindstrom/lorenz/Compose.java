package dk.jonaslindstrom.lorenz;

import dk.jonaslindstrom.mosef.midi.MIDIEncoder;
import dk.jonaslindstrom.mosef.modules.melody.Note;
import dk.jonaslindstrom.mosef.modules.melody.Track;
import dk.jonaslindstrom.mosef.modules.scales.Scale;
import dk.jonaslindstrom.mosef.modules.scales.ScaleFactory;
import dk.jonaslindstrom.mosef.modules.scales.ScaleFactory.Key;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.sound.midi.InvalidMidiDataException;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

public class Compose {

  public static void main(String[] args)
      throws InvalidMidiDataException, IOException {

    // Configuration
    int voices = 3; // The number of voices to output
    double length = 480; // Target length in seconds with 120 bpm
    long seed = 6674089274190705457L;
    Scale scale = ScaleFactory.minor(Key.C);
    int k = 7; // Map every k'th step of the integrator to a note
    double t1 = 100; // should be large enough to fit target length
    Random random = new Random(seed);

    // Parameters for sampling initial points
    double[] μ = {20, 30, 30};
    double[] σ = {20, 20, 20};

    // Define the Lorenz system
    LorenzEquations equations = new LorenzEquations(10, 28, 2);

    // Generate tracks
    for (int v = 0; v < voices; v++) {
      Track track = new Track();
      Vector3D init = new Vector3D(IntStream.range(0, 3).sequential()
          .mapToDouble(i -> random.nextGaussian() * σ[i] + μ[i]).toArray());

      List<Interval> range = new ArrayList<>();
      Consumer<List<Interval>> rangeConsumer = range::addAll;

      PointProcessor<Double> consumer = (point, t, i) -> {
        if (FastMath.floorMod(i, k) == 0) {
          Note note = mapToNote(point, t, scale, range.get(1), range.get(2));
          track.addNote(note);
          return t + note.getDuration();
        } else {
          return t;
        }
      };

      Lorenz
          .run(init, equations, t1, consumer, rangeConsumer, 0.0, t -> t >= length);
      MIDIEncoder.encode(track, "lorenz" + v + ".mid");
    }
  }

  /**
   * Map a point on the curve to a note at time t using the given scale.
   */
  public static Note mapToNote(Vector3D point, double t, Scale scale, Interval y,
      Interval z) {
    double ticks = FastMath.floor(Util.mapToRange(point.getY(), y, new Interval(-1, 4)));
    double duration = FastMath.pow(2, -ticks);
    int note = (int) Util.mapToRange(point.getZ(), z, new Interval(14, 55));
    return new Note(
        scale.noteAt(note), t, 0.5, duration);
  }

}
