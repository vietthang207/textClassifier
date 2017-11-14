import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Collections;

class Main {
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		if (args.length >= 1) {
			if (args[0].equals("tc_train") && args.length == 4) {
				tcTrain(args[1], args[2], args[3]);
				return;
			}
			else if (args[0].equals("tc_test") && args.length == 5) {
				double accuracy = tcTest(args[1], args[2], args[3], args[4]);
				System.out.println("Test accuracy: " + accuracy);
				return;
			}
			// else if (args[0].equals("cross_validation") && args.length == 4) {
			// 	trainWithCrossValidationOn(args[1]);
			// 	return;
			// }	
		}
		System.out.println("wrong command");
	}
	
	public static void tcTrain(String stopWordsFile, String trainClassListFile, String modelName) throws IOException, ClassNotFoundException {
		HashSet<String> stopWords = Preprocessor.getStopWordFromFile(stopWordsFile);
		ArrayList<Sample> samples = new ArrayList<Sample>();
		HashSet<String> classesSet = new HashSet<String>();
		DataReader dataReader = new DataReader(trainClassListFile);
		String line;
		while ( (line = dataReader.nextLine()) != null) {
			String[] tokens = line.split(" ");
			classesSet.add(tokens[1]);
			samples.add(new Sample(tokens[1], tokens[0], stopWords));
		}
		dataReader.close();
		String[] classNames = new String[classesSet.size()];
		int counter = 0;
		for (String n: classesSet) {
			classNames[counter] = n;
			counter ++;
		}
		// Hyperparameters hyperParams = gridSearchHyperparameters(classNames, samples);
		Hyperparameters hyperParams = new Hyperparameters(10.0, 10, 1, 50);
		double cvAccuracy = nFoldCrossValidation(5, classNames, samples, hyperParams);
		System.out.println("5-fold crossvalidation accuracy: " + cvAccuracy);
		Model model = train(classNames, samples, hyperParams);
		saveModel(model, modelName);
		double trainingAccuracy = tcTest(stopWordsFile, modelName, "", trainClassListFile);
		System.out.println("Training accuracy: " + trainingAccuracy);
	}

	public static Hyperparameters gridSearchHyperparameters(String[] classNames, ArrayList<Sample> samples) {
		double[] chiSquaresThresholds = {0.1, 1, 10, 100};
		int[] kThresholds = {10, 100};
		double[] learningRates = {0.01, 0.1, 1, 10, 100};
		int[] numIters = {50};

		double bestCvAccuracy = Double.NEGATIVE_INFINITY;
		Hyperparameters bestHyperparams = null;
		for (double c: chiSquaresThresholds) {
			for (int k: kThresholds) {
				for (double l: learningRates) {
					for (int n: numIters) {
						Hyperparameters h = new Hyperparameters(c, k, l, n);
						double cvAccuracy = nFoldCrossValidation(5, classNames, samples, h);
						System.out.println("Hyperparams: " + h);
						System.out.println("Cross validation accuracy: " + cvAccuracy);
						if (cvAccuracy > bestCvAccuracy) {
							bestCvAccuracy = cvAccuracy;
							bestHyperparams = h;
						}
					}
				}
			}
		}
		return bestHyperparams;
	}

	private static Model train(String[] classNames, ArrayList<Sample> samples, Hyperparameters hyperParams) {
		// System.out.println("train on " + hyperParams);
		Model model = new Model(classNames, hyperParams);
		// System.out.println("Finish reading " + samples.size() + " training samples");
		for (Sample s: samples) {
			model.addSample(s);
		}
		model.train();
		return model;
	}

	public static double nFoldCrossValidation(int n, String[] classNames, ArrayList<Sample> samples, Hyperparameters hyperParams) {
		ArrayList<Integer> randomArray = new ArrayList<Integer>(samples.size());
		for (int i=0; i<samples.size(); i++) {
			randomArray.add(i);
		}
		
		ArrayList<Double> validationError = new ArrayList<Double>();
		
		for (int iter=0; iter<n; iter++) {
			System.out.println("Cross Validation iteration " + (iter + 1));
			Collections.shuffle(randomArray);
			// N/10 first element will be put to validation set
			HashSet<Integer> validationIndex = new HashSet<Integer>();
			for (int i=0; i<samples.size()/n; i++) {
				validationIndex.add(randomArray.get(i));
			}
			ArrayList<Sample> trainingSamples = new ArrayList<Sample>();			
			ArrayList<Sample> validationSamples = new ArrayList<Sample>();
			for (int i=0; i<samples.size(); i++) {
				Sample s = samples.get(i);
				if (validationIndex.contains(i)) {
					validationSamples.add(s);
				} else {
					trainingSamples.add(s);
				}
			}
			Model model = train(classNames, trainingSamples, hyperParams);
			double accuracy = test(model, validationSamples);
			validationError.add(accuracy);
			System.out.println("Accuracy on validation set " + accuracy);
		}
		double cvError = 0;
		for (double d: validationError) {
			cvError += d;
		}
		cvError /= validationError.size();
		return cvError;
	}

	public static double tcTest(String stopWordsFile, String modelName, String testListFile, String testClassListFile) throws IOException, ClassNotFoundException {
		Model model = loadModel(modelName);
		HashSet<String> stopWords = Preprocessor.getStopWordFromFile(stopWordsFile);
		DataReader dataReader = new DataReader(testClassListFile);
		ArrayList<Sample> samples = new ArrayList<Sample>();
		String line;
		while ( (line = dataReader.nextLine()) != null) {
			String[] tokens = line.split(" ");
			samples.add(new Sample(tokens[1], tokens[0], stopWords));
		}
		dataReader.close();
		return test(model, samples);
	}

	private static double test(Model model, ArrayList<Sample> samples) {
		int total = 0;
		int correct = 0;
		for (Sample s: samples) {
			String prediction = model.predict(s);
			// System.out.println(prediction);
			if (prediction.equals(s.className)) {
				correct ++;
			}
			total ++;
		}
		return correct * 100.0 / total;
	}

	private static void saveModel(Model model, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(model);
	}

	private static Model loadModel(String fileName) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		return (Model) ois.readObject();
	}
}