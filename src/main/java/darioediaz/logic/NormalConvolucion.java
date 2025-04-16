package darioediaz.logic;

import darioediaz.interfaces.IDistributionGenerator;

import java.util.Random;
import java.util.stream.DoubleStream;

public class NormalConvolucion implements IDistributionGenerator {

	private final double mean, stddev;

	public NormalConvolucion(double mean, double stddev) {
		this.mean = mean;
		this.stddev = stddev;
	}

	@Override
	public double[] generateSamples(int sampleSize) {
		Random rand = new Random();
		return DoubleStream.generate(() -> mean + stddev * rand.nextGaussian())
				.limit(sampleSize)
				.toArray();
	}

	private double normalCDF(double x) {
		return 0.5 * (1 + erf((x - mean) / (stddev * Math.sqrt(2))));
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

	@Override
	public double probability(double from, double to) {
		return normalCDF(to) - normalCDF(from);
	}

}
