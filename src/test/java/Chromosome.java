import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
	public double[][] distanceRanges; // 0 - 16000
	public double[][] angleRanges; // 0 - 360
	public double[][][][] thrustInRange; // 0 - 200

	public double[] varianceDistance;
	public double[] varianceAngle;
	public double[][] varianceThrust;

	public double fitness = 1000.0;

	/**
	 * Crea un individuo a partir del padre y muta.
	 *
	 * @param c: cromosoma del padre
	 */
	public Chromosome(Chromosome c) {
		Random rand = new Random();

		// Mutación del vector de distancias
		this.distanceRanges = new double[2][c.distanceRanges[0].length];
		for (int i = 0; i < c.distanceRanges[0].length; i++) {
			this.distanceRanges[0][i] = Math.abs(c.distanceRanges[0][i] + rand.nextGaussian() * c.varianceDistance[i]);
			this.distanceRanges[1][i] = Math.abs(c.distanceRanges[1][i] + rand.nextGaussian() * c.varianceDistance[i]);
		}

		// Mutación vector de ángulos
		this.angleRanges = new double[2][c.angleRanges[0].length];
		for (int i = 0; i < c.angleRanges[0].length; i++) {
			this.angleRanges[0][i] = (c.angleRanges[0][i] + rand.nextGaussian() * c.varianceAngle[i]) % 360;
			this.angleRanges[0][i] = (this.angleRanges[0][i] + 360) % 360;
			this.angleRanges[1][i] = (c.angleRanges[1][i] + rand.nextGaussian() * c.varianceAngle[i]) % 360;
			this.angleRanges[1][i] = (this.angleRanges[1][i] + 360) % 360;
		}

		// Mutación vector de velocidades
		this.thrustInRange = new double[c.thrustInRange.length][c.thrustInRange[0].length][c.thrustInRange[0][0].length][c.thrustInRange[0][0][0].length];
		for (int i = 0; i < c.thrustInRange.length; i++)
			for (int j = 0; j < c.thrustInRange[0].length; j++)
				for (int i2 = 0; i2 < c.thrustInRange[0][0].length; i2++)
					for (int j2 = 0; j2 < c.thrustInRange[0][0][0].length; j2++)
						this.thrustInRange[i][j][i2][j2] = Math.min(Math.abs(
								c.thrustInRange[i][j][i2][j2] + rand.nextGaussian() * c.varianceThrust[i2][j2]), 200);

		// Copia de varianzas
		this.varianceDistance = new double[c.varianceDistance.length];
		System.arraycopy(c.varianceDistance, 0, this.varianceDistance, 0, c.varianceDistance.length);

		this.varianceAngle = new double[c.varianceAngle.length];
		System.arraycopy(c.varianceAngle, 0, this.varianceAngle, 0, c.varianceAngle.length);

		this.varianceThrust = new double[c.varianceThrust.length][c.varianceThrust[0].length];
		for (int i = 0; i < this.varianceThrust.length; i++)
			System.arraycopy(c.varianceThrust[i], 0, this.varianceThrust[i], 0, this.varianceThrust[i].length);

		writeChromosome("files/chromosome.csv");
	}

	/**
	 * Crea un cromosoma a partir de dos padres. Luego lo muta.
	 * 
	 * @param c1: padre 1
	 * @param c2: padre 2
	 */
	public Chromosome(Chromosome c1, Chromosome c2) {
		Random rand = new Random();

		// Cruce de varianza de distancias
		this.varianceDistance = new double[c1.varianceDistance.length];
		for (int i = 0; i < this.varianceDistance.length; i++)
			if (Math.random() * 2 < 1) this.varianceDistance[i] = c1.varianceDistance[i];
			else this.varianceDistance[i] = c2.varianceDistance[i];

		// Cruce de varianza de ángulos
		this.varianceAngle = new double[c1.varianceAngle.length];
		for (int i = 0; i < this.varianceAngle.length; i++)
			if (Math.random() * 2 < 1) this.varianceAngle[i] = c1.varianceAngle[i];
			else this.varianceAngle[i] = c2.varianceAngle[i];

		// Cruce de varianza de velocidades
		this.varianceThrust = new double[c1.varianceThrust.length][c1.varianceThrust[0].length];
		for (int i = 0; i < this.varianceThrust.length; i++)
			for (int j = 0; j < this.varianceThrust[i].length; j++)
				if (Math.random() * 2 < 1) this.varianceThrust[i][j] = c1.varianceThrust[i][j];
				else this.varianceThrust[i][j] = c2.varianceThrust[i][j];

		// Mutación del vector de distancias
		this.distanceRanges = new double[2][c1.distanceRanges[0].length];
		for (int i = 0; i < this.distanceRanges[0].length; i++) {
			this.distanceRanges[0][i] = (c1.distanceRanges[0][i] + c2.distanceRanges[0][i]) / 2;
			this.distanceRanges[0][i] = Math
					.abs(this.distanceRanges[0][i] + rand.nextGaussian() * this.varianceDistance[i]);
			this.distanceRanges[1][i] = (c1.distanceRanges[1][i] + c2.distanceRanges[1][i]) / 2;
			this.distanceRanges[1][i] = Math
					.abs(this.distanceRanges[1][i] + rand.nextGaussian() * this.varianceDistance[i]);
		}

		// Mutación del vector de ángulos
		this.angleRanges = new double[2][c1.angleRanges[0].length];
		for (int i = 0; i < this.angleRanges[0].length; i++) {
			this.angleRanges[0][i] = (c1.angleRanges[0][i] + c2.angleRanges[0][i]) / 2;
			this.angleRanges[0][i] = Math.abs(this.angleRanges[0][i] + rand.nextGaussian() * this.varianceAngle[i]);
			this.angleRanges[0][i] = (this.angleRanges[0][i] + 360) % 360;
			this.angleRanges[1][i] = (c1.angleRanges[1][i] + c2.angleRanges[1][i]) / 2;
			this.angleRanges[1][i] = Math.abs(this.angleRanges[1][i] + rand.nextGaussian() * this.varianceAngle[i]);
			this.angleRanges[1][i] = (this.angleRanges[1][i] + 360) % 360;
		}

		// Mutación del vector de velocidades
		this.thrustInRange = new double[c1.thrustInRange.length][c1.thrustInRange[0].length][c1.thrustInRange[0][0].length][c1.thrustInRange[0][0][0].length];
		for (int i = 0; i < this.thrustInRange.length; i++)
			for (int j = 0; j < this.thrustInRange[0].length; j++)
				for (int i2 = 0; i2 < this.thrustInRange[0][0].length; i2++)
					for (int j2 = 0; j2 < this.thrustInRange[0][0][0].length; j2++) {
						this.thrustInRange[i][j][i2][j2] = (c1.thrustInRange[i][j][i2][j2]
								+ c2.thrustInRange[i][j][i2][j2]) / 2;
						this.thrustInRange[i][j][i2][j2] = Math.min(Math.abs(
								this.thrustInRange[i][j][i2][j2] + rand.nextGaussian() * this.varianceThrust[i2][j2]),
								200);
					}
	}

	/**
	 * Crea un cromosoma a partir de la información de un CSV
	 *
	 * @param path: ruta del fichero CSV
	 */
	public Chromosome(String path) {
		this.distanceRanges = new double[2][];
		this.angleRanges = new double[2][];
		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			// Extrae las distancias del fichero
			String distanceRangesLine = bufferedReader.readLine();
			int numDistanceRanges = distanceRangesLine.split(",").length;
			this.distanceRanges[0] = convertToDouble(distanceRangesLine.split(","));
			distanceRangesLine = bufferedReader.readLine();
			this.distanceRanges[1] = convertToDouble(distanceRangesLine.split(","));

			// Extrae los ángulos del fichero
			String angleRangesLine = bufferedReader.readLine();
			int numAngleRanges = angleRangesLine.split(",").length;
			this.angleRanges[0] = convertToDouble(angleRangesLine.split(","));
			angleRangesLine = bufferedReader.readLine();
			this.angleRanges[1] = convertToDouble(angleRangesLine.split(","));

			// Extrae las velocidades del fichero
			this.thrustInRange = new double[numDistanceRanges + 1][numAngleRanges + 1][numDistanceRanges
					+ 1][numAngleRanges + 1];
			for (int i = 0; i < numDistanceRanges + 1; i++)
				for (int j = 0; j < numAngleRanges + 1; j++)
					for (int i2 = 0; i2 < numDistanceRanges + 1; i2++)
						this.thrustInRange[i][j][i2] = convertToDouble(bufferedReader.readLine().split(","));

			// Inicializa las varianzas.
			this.varianceDistance = new double[numDistanceRanges];
			this.varianceAngle = new double[numAngleRanges];
			this.varianceThrust = new double[numDistanceRanges + 1][numAngleRanges + 1];
			bufferedReader.close();

		} catch (Exception e) {
			System.err.println(e);
		}
		this.initializeVariances();
	}

	/**
	 * Función que ayuda a ordenar la lista de cromosomas según el fitness.
	 * 
	 * @param c: cromosoma a comparar con este
	 * @return -1, 0, 1: dependiendo si la comparación resulta menor, igual o mayor.
	 */
	@Override
	public int compareTo(Chromosome c) {
		return Double.compare(this.fitness, c.fitness);
	}

	/**
	 * Inicializa las varianzas aleatoriamente
	 */
	public void initializeVariances() {
		// En orden: distancia, ángulo, aceleración minima y máxima en la inicialización
		double[][] stndDer = { { 100, 1000 }, { 10, 180 }, { 10, 50 } };

		// Inicialización vector de varianzas de distancias
		for (int i = 0; i < this.varianceDistance.length; i++)
			this.varianceDistance[i] = Math.random() * (stndDer[0][1] - stndDer[0][0]) + stndDer[0][0];

		// Inicialización vector de varianzas de ángulos
		for (int i = 0; i < this.varianceAngle.length; i++)
			this.varianceAngle[i] = Math.random() * (stndDer[1][1] - stndDer[1][0]) + stndDer[1][0];

		// Inicialización matriz de varianzas velocidades
		for (int i = 0; i < this.varianceThrust.length; i++)
			for (int j = 0; j < this.varianceThrust[0].length; j++)
				this.varianceThrust[i][j] = Math.random() * (stndDer[2][1] - stndDer[2][0]) + stndDer[2][0];
	}

	/**
	 * Convierte la entrada String del csv a un array de double.
	 *
	 * @param values: valores del csv en formato String.
	 * @return convertedValues: array de valores en formato double.
	 */
	private double[] convertToDouble(String[] values) {
		double[] convertedValues = new double[values.length];
		for (int i = 0; i < values.length; i++)
			convertedValues[i] = Double.parseDouble(values[i]);
		return convertedValues;
	}

	/**
	 * Guarda la información de un cromosoma en formato csv.
	 *
	 * @param file: fichero en el que se copia el individuo.
	 */
	public void writeChromosome(String file) {
		FileWriter myWriter;
		try {
			myWriter = new FileWriter(file);

			// Guarda la información de las distancias
			for (double[] distanceRange : this.distanceRanges) {
				for (int i = 0; i < distanceRange.length; i++) {
					if (i == distanceRange.length - 1) {
						myWriter.write(distanceRange[i] + "\n");
					} else {
						myWriter.write(distanceRange[i] + ",");
					}
				}
			}

			// Guarda la información de los ángulos
			for (double[] angleRange : this.angleRanges) {
				for (int i = 0; i < angleRange.length; i++) {
					if (i == angleRange.length - 1) {
						myWriter.write(angleRange[i] + "\n");
					} else {
						myWriter.write(angleRange[i] + ",");
					}
				}
			}

			// Guarda la información de las velocidades
			for (double[][][] doubles : this.thrustInRange) {
				for (double[][] aDouble : doubles) {
					for (double[] value : aDouble) {
						for (int j2 = 0; j2 < value.length; j2++) {
							if (j2 == value.length - 1) {
								myWriter.write(value[j2] + "\n");
							} else {
								myWriter.write(value[j2] + ",");
							}
						}
					}
				}
			}

			myWriter.write(this.fitness + "\n");
			myWriter.flush();
			myWriter.close();

		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
