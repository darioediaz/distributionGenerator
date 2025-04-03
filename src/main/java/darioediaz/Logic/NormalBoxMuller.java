package darioediaz.Logic;

import darioediaz.Interfaces.DistributionGenerator;

import java.util.Random;

public class NormalBoxMuller implements DistributionGenerator {

	private final double average, devation;
	private Random rnd = new Random();

	public NormalBoxMuller(double devation, double average) {
		this.devation = devation;
		this.average = average;
	}

	@Override
	public double[] generateSamples(int sampleSize) {
		double[] samples = new double[sampleSize];

		for (int i = 0; i < sampleSize; i+=2) {

			double rnd1 = rnd.nextDouble();
			double rnd2 = rnd.nextDouble();

			double radio = Math.sqrt(-2.0 * Math.log(rnd1));
			double theta = 2.0 * Math.PI * rnd2;

			double z0 = radio * Math.cos(theta);
			double z1 = radio * Math.sin(theta);

			samples[i] = z0 * devation + average;
			if (i + 1 < sampleSize) {
				samples[i + 1] = z1 * devation + average;
			}
		}

		return samples;
	}
}
