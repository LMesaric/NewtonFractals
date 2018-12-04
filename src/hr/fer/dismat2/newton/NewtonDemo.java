package hr.fer.dismat2.newton;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;

import javax.imageio.ImageIO;

public class NewtonDemo {

	private static final Color[] COLORS = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA,
			new Color(255, 155, 0), Color.PINK };

	// 4K resolution is 3840 x 2160
	private static final int WIDTH = 3840 * 6;
	private static final int HEIGHT = 2160 * 6;

	private static final double EPSILON = 0.0008;
	private static final int MAX_ITER = 55;

	private static int ROOT;
	private static Complex[] ROOTS;

	public void run() {
		for (ROOT = 2; ROOT <= 8; ROOT++) {
			ROOTS = Complex.ONE.nroots(ROOT);
			exportImage();
		}
	}

	private int[] generateRgbs() {
		int[] rgbs = new int[WIDTH * HEIGHT];

		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				rgbs[y * WIDTH + x] = calculateColor(x, y).getRGB();
			}
		}

		return rgbs;
	}

	private Color calculateColor(int x, int y) {
		Complex complex = calculateComplexFromCoordinates(x, y);
		Tuple iterData = numberOfIterations(complex);

		if (iterData == null) {
			return Color.BLACK;
		}

		return darker(COLORS[iterData.k], Math.pow(0.984, iterData.numIter - 1));
	}

	private Color darker(Color color, double factor) {
		return new Color(Math.max((int) (color.getRed() * factor), 0), Math.max((int) (color.getGreen() * factor), 0),
				Math.max((int) (color.getBlue() * factor), 0), color.getAlpha());
	}

	private Complex calculateComplexFromCoordinates(int x, int y) {
		double a = (1.0 * WIDTH) / HEIGHT;
		return new Complex(-a + (2.0 * a * x) / (WIDTH - 1), 1.0 + (2.0 * y) / (1 - HEIGHT));
	}

	private Tuple numberOfIterations(Complex complex) {
		int n;
		for (n = 1; n <= MAX_ITER; n++) {
			OptionalInt isClose = isCloseEnough(complex);
			if (isClose.isPresent()) {
				return new Tuple(n, isClose.getAsInt());
			}
			complex = complex.scale(ROOT - 1).plus(complex.pow(1 - ROOT)).scale(1.0 / ROOT);
		}
		return null;
	}

	private OptionalInt isCloseEnough(Complex complex) {
		for (int k = 0; k < ROOTS.length; k++) {
			if (complex.dist(ROOTS[k]) < EPSILON) {
				return OptionalInt.of(k);
			}
		}
		return OptionalInt.empty();
	}

	private void exportImage() {
		int[] rgbs = generateRgbs();
		DataBuffer rgbData = new DataBufferInt(rgbs, rgbs.length);
		WritableRaster raster = Raster.createPackedRaster(rgbData, WIDTH, HEIGHT, WIDTH,
				new int[] { 0xff0000, 0xff00, 0xff }, null);
		ColorModel colorModel = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
		BufferedImage img = new BufferedImage(colorModel, raster, false, null);

		String directoryPath = WIDTH + "x" + HEIGHT;
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

		String fname = String.format("%s%sRoot%d_%s_LukaMesaric.png", directoryPath, File.separator, ROOT,
				directoryPath);

		try {
			ImageIO.write(img, "png", new File(fname));
		} catch (IOException e) {
			System.out.println("FAILED to export " + fname);
			e.printStackTrace();
			return;
		}

		System.out.println("Exported to " + fname);
	}

	public static void main(String[] args) {
		new NewtonDemo().run();
		System.out.println("ALL DONE!");
	}

	private static class Tuple {
		private int numIter;
		private int k;

		private Tuple(int numIter, int k) {
			this.numIter = numIter;
			this.k = k;
		}
	}

}
