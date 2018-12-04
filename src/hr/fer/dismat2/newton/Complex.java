package hr.fer.dismat2.newton;

public class Complex {

	public static final Complex ONE = new Complex(1, 0);

	private final double re;
	private final double im;

	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}

	public double abs() {
		return Math.hypot(re, im);
	}

	public double dist(Complex other) {
		return this.minus(other).abs();
	}

	public double phase() {
		return Math.atan2(im, re);
	}

	public Complex plus(Complex b) {
		double real = this.re + b.re;
		double imag = this.im + b.im;
		return new Complex(real, imag);
	}

	public Complex minus(Complex b) {
		return this.plus(b.scale(-1));
	}

	public Complex scale(double alpha) {
		return new Complex(alpha * re, alpha * im);
	}

	public Complex[] nroots(int n) {
		Complex[] nroots = new Complex[n];

		double module = Math.pow(abs(), 1.0 / n);
		double phase = phase();

		for (int k = 0; k < n; k++) {
			double phaseTemp = (phase + 2 * Math.PI * k) / n;
			nroots[k] = new Complex(module * Math.cos(phaseTemp), module * Math.sin(phaseTemp));
		}

		return nroots;
	}

	public Complex pow(int power) {
		double module = Math.pow(abs(), power);
		double phase = phase() * power;
		return new Complex(module * Math.cos(phase), module * Math.sin(phase));
	}

}
