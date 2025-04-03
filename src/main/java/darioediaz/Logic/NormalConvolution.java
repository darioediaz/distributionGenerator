package darioediaz.Logic;

import darioediaz.Interfaces.DistributionGenerator;

import java.util.Random;
import java.util.stream.DoubleStream;

public class NormalConvolution implements DistributionGenerator {

	private final double average, devation;
	private Random rnd = new Random();

	public NormalConvolution(double devation, double average) {
		this.devation = devation;
		this.average = average;
	}

	@Override
	public double[] generateSamples(int sampleSize) {
		return DoubleStream.generate(() -> average + devation * rnd.nextGaussian())
				.limit(sampleSize).toArray();
	}
}
