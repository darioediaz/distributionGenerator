package darioediaz.logic;

import darioediaz.interfaces.IDistributionGenerator;

import java.util.stream.DoubleStream;

public class Uniforme implements IDistributionGenerator {

	private final double a, b;

	public Uniforme(double a, double b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double[] generateSamples(int sampleSize) {
		return DoubleStream.generate(() -> a + Math.random() * (b - a))
				.limit(sampleSize).toArray();
	}

	@Override
	public double probability(double from, double to) {
		double lower = Math.max(from, a);
		double upper = Math.min(to, b);
		return (upper - lower) / (b - a);
	}
}