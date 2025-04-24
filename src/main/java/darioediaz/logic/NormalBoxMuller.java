package darioediaz.logic;

import darioediaz.interfaces.IDistributionGenerator;

public class NormalBoxMuller implements IDistributionGenerator {

  private final double avg, stdDeviation;

  public NormalBoxMuller(double avg, double stdDeviation) {
    this.avg = avg;
    this.stdDeviation = stdDeviation;
  }

  @Override
  public double[] generateSamples(int sampleSize) {
    double[] samples = new double[sampleSize];
    for (int i = 0; i < sampleSize; i += 2) {
      double rnd1 = Math.random();
      double rnd2 = Math.random();

      double radio = Math.sqrt(-2.0 * Math.log(rnd1));
      double theta = 2.0 * Math.PI * rnd2;

      double z0 = radio * Math.cos(theta);
      double z1 = radio * Math.sin(theta);

      samples[i] = z0 * stdDeviation + avg;
      if (i + 1 < sampleSize) {
        samples[i + 1] = z1 * stdDeviation + avg;
      }
    }
    return samples;
  }

  @Override
  public double probability(double from, double to) {
    return normalCDF(to) - normalCDF(from);
  }

  private double normalCDF(double x) {
    return 0.5 * (1 + erf((x - avg) / (stdDeviation * Math.sqrt(2))));
  }

  private double erf(double z) {
    double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
    double ans = 1 - t * Math.exp(-z * z - 1.26551223 +
        t * (1.00002368 +
            t * (0.37409196 +
                t * (0.09678418 +
                    t * (-0.18628806 +
                        t * (0.27886807 +
                            t * (-1.13520398 +
                                t * (1.48851587 +
                                    t * (-0.82215223 +
                                        t * 0.17087277)))))))));
    return z >= 0 ? ans : -ans;
  }
}
