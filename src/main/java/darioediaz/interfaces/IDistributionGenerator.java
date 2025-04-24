package darioediaz.interfaces;


public interface IDistributionGenerator {

  double[] generateSamples(int sampleSize);

  double probability(double from, double to);

}

