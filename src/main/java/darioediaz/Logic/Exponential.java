package darioediaz.Logic;

import darioediaz.Interfaces.DistributionGenerator;

import java.util.Random;
import java.util.stream.DoubleStream;

public class Exponential implements DistributionGenerator {

	private final double lambda;
	private Random rnd = new Random();

	public Exponential(double lambda) {
		this.lambda = lambda;
	}

	@Override
	public double[] generateSamples(int sampleSize) {
		return DoubleStream.generate(() -> -Math.log(1 - rnd.nextDouble()) / lambda).limit(sampleSize).toArray();
	}
}
