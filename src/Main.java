import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

class Main {
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		if (args.length >= 1) {
			if (args[0].equals("tc_train") && args.length == 4) {
				tcTrain(args[1], args[2], args[3]);
				return;
			}
			else if (args[0].equals("tc_test") && args.length == 5) {
				tcTest(args[1], args[2], args[3], args[4]);
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
		Model model = new Model(classNames, 100, 100, 0.001, 500);
		System.out.println("Finish reading " + samples.size() + " training samples");
		for (Sample s: samples) {
			// System.out.print(s.className);
			model.addSample(s);
		}
		model.train();
		saveModel(model, modelName);
		System.out.println("Evaluate on traning set:");
		tcTest(stopWordsFile, modelName, "", trainClassListFile);
	}

	public static void tcTest(String stopWordsFile, String modelName, String testListFile, String testClassListFile) throws IOException, ClassNotFoundException {
		Model model = loadModel(modelName);
		HashSet<String> stopWords = Preprocessor.getStopWordFromFile(stopWordsFile);
		DataReader dataReader = new DataReader(testClassListFile);
		int total = 0;
		int correct = 0;
		String line;
		while ( (line = dataReader.nextLine()) != null) {
			String[] tokens = line.split(" ");
			String prediction = model.predict(new Sample(tokens[1], tokens[0], stopWords));
			System.out.println(prediction);
			if (prediction.equals(tokens[1])) {
				correct ++;
			}
			total ++;
		}
		System.out.println("Accuracy: " + correct * 100.0 / total + " %");
		dataReader.close();
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