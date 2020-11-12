package dk.jonaslindstrom.lorenz;

import dk.jonaslindstrom.mosef.midi.MIDIEncoder;
import dk.jonaslindstrom.mosef.modules.melody.Note;
import dk.jonaslindstrom.mosef.modules.melody.Track;
import dk.jonaslindstrom.mosef.modules.scales.Scale;
import dk.jonaslindstrom.mosef.modules.scales.ScaleFactory;
import dk.jonaslindstrom.mosef.modules.scales.ScaleFactory.Key;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.sound.midi.InvalidMidiDataException;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.LegacyListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;

public class Compose {

  public static void main(String[] args)
      throws InvalidMidiDataException, IOException, ConfigurationException {

    FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
        new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
            .configure(new Parameters().properties().setFile(new File("lorenz.properties")).setListDelimiterHandler(new LegacyListDelimiterHandler(',')));
    Configuration config = builder.getConfiguration();

    // Configuration
    int voices = config.getInt("voices");
    double length = config.getDouble("length");
    long seed = config.getLong("seed");
    int k = config.getInt("k");
    double t1 = 2 * length; // should be large enough to fit target length. Longest possible note is 2 seconds

    // Parameters for sampling initial points
    double[] μ = Arrays.stream(config.getStringArray("mu")).mapToDouble(Double::valueOf).toArray();
    double[] σ = Arrays.stream(config.getStringArray("sigma")).mapToDouble(Double::valueOf).toArray();

    Scale scale = ScaleFactory.minor(Key.C);
    Random random = new Random(seed);

    // Define the Lorenz system
    LorenzEquations equations = new LorenzEquations(
        config.getDouble("s"),
        config.getDouble("r"),
        config.getDouble("b"));

    // Generate tracks
    for (int v = 0; v < voices; v++) {
      Track track = new Track();
      Vector3D init = new Vector3D(IntStream.range(0, 3 /* dimensions */).sequential()
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
  private static Note mapToNote(Vector3D point, double t, Scale scale, Interval y,
      Interval z) {
    double ticks = FastMath.floor(Util.mapToRange(point.getY(), y, new Interval(-1, 4)));
    double duration = FastMath.pow(2, -ticks);
    int note = (int) Util.mapToRange(point.getZ(), z, new Interval(14, 55));
    return new Note(
        scale.noteAt(note), t, 0.5, duration);
  }

}
