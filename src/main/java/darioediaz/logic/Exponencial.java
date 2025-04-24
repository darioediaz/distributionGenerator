package darioediaz.logic;

import darioediaz.interfaces.IDistributionGenerator;

import java.util.stream.DoubleStream;

public class Exponencial implements IDistributionGenerator {

  private final double lambda;

  public Exponencial(double lambda) {
    this.lambda = lambda;
  }

  @Override
  public double[] generateSamples(int sampleSize) {
    return DoubleStream.generate(() -> -Math.log(1 - Math.random()) / lambda)
        .limit(sampleSize).toArray();
  }

  @Override
  public double probability(double from, double to) {
    return Math.exp(-lambda * from) - Math.exp(-lambda * to);
  }
}
