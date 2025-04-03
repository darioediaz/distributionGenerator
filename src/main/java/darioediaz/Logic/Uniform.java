package darioediaz.Logic;

import darioediaz.Interfaces.DistributionGenerator;

import java.util.Random;
import java.util.stream.DoubleStream;

public class Uniform implements DistributionGenerator {

	private final double a, b;
	private final Random rnd = new Random();

	public Uniform(double a, double b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double[] generateSamples(int sampleSize) {
		return DoubleStream.generate(() -> a + rnd.nextDouble() * (b - a))
				.limit(sampleSize).toArray();
	}

}
