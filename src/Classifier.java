import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

class Classifier implements Serializable {

	String className;
	double chiSquareThreshold;
	int kThreshold;
	double learningRate;
	int numIteration;
	HashSet<String> vocab;
	HashMap<String, Integer> vocabCount;
	int numPositiveText;
	int numNegativeText;	
	HashMap<String, Integer> positiveCount;
	HashMap<String, Integer> negativeCount;
	ArrayList<String> features;
	double[] weights;
	ArrayList<Sample> samples;

	public Classifier(String className, double chiSquareThreshold, int kThreshold, double learningRate, int numIteration) {
		this.className = className;
		this.chiSquareThreshold = chiSquareThreshold;
		this.kThreshold = kThreshold;
		this.learningRate = learningRate;
		this.numIteration = numIteration;
		vocab = new HashSet<String>();
		vocabCount = new HashMap<String, Integer>();
		numPositiveText = numNegativeText = 0;
		positiveCount = new HashMap<String, Integer>();
		negativeCount = new HashMap<String, Integer>();
		features = new ArrayList<String>();
		samples = new ArrayList<Sample>();
	}

	public void addSample(Sample s) {
		samples.add(s);
		for (String w: s.words) {
			if (vocab.contains(w)) {
				vocabCount.put(w, vocabCount.get(w) + 1);
			} else {
				vocab.add(w);
				vocabCount.put(w, 1);			
			}
		}
		if (s.className.equals(this.className)) {
			numPositiveText ++;
			for (String w: s.words) {
				if (positiveCount.containsKey(w)) {
					positiveCount.put(w, positiveCount.get(w) + 1);
				} else {
					positiveCount.put(w, 1);
				}
			}
		} else {
			numNegativeText ++;
			for (String w: s.words) {
				if (negativeCount.containsKey(w)) {
					negativeCount.put(w, negativeCount.get(w) + 1);
				} else {
					negativeCount.put(w, 1);	
				}
			}
		}
	}

	public void runFeatureSelection() {
		for (String w: vocab) {
			if (vocabCount.get(w) < kThreshold) {
				continue;
			}
			long n11, n10, n01, n00;
			n11 = n10 = n01 = n00 = 1;
			if (positiveCount.containsKey(w)) {
				n11 += positiveCount.get(w);
				n01 += numPositiveText - positiveCount.get(w);
			}
			if (negativeCount.containsKey(w)) {
				n10 += negativeCount.get(w);
				n00 += numNegativeText - negativeCount.get(w);
			}
			double chiSquare = 1.0 * (n11+n10+n01+n00) * (n11*n00 - n10*n01) * (n11*n00 - n10*n01);
			chiSquare = chiSquare / (1.0 * (n11+n01) * (n11+n10) * (n10+n00) * (n01 + n00));

			if (chiSquare > chiSquareThreshold) {
				features.add(w);
			}
		}
		System.out.println("Number of features of " + className + " : " + features.size());
	}

	public void initializeWeights() {
		weights = new double[features.size()+1];
		// Random rd = new Random();
		// for (int i=0; i<weights.length; i++) {
		// 	weights[i] = rd.nextDouble() - 0.5;
		// }
	}

	private double getLabel(Sample s) {
		if (s.className.equals(className)) return 1;
		return 0;
	}

	public double[] getFeatureVector(Sample s) {
		HashMap<String, Integer> count = new HashMap<String, Integer>();
		double[] vector = new double[features.size() + 1];
		for (String w: s.words) {
			if (count.containsKey(w)) {
				count.put(w, count.get(w) + 1);
			} else {
				count.put(w, 1);
			}
		}
		vector[0] = 1.0/features.size();
		for (int i=0; i<features.size(); i++) {
			if (count.containsKey(features.get(i))) {
				vector[i + 1] = 1.0 * count.get(features.get(i)) / s.words.length;	
			} else {
				vector[i + 1] = 0;
			}
		}
		return vector;
	}

	public double predict(Sample s) {
		double[] vector = getFeatureVector(s);
		double res = 0;
		for (int i=0; i<vector.length; i++) {
			res += weights[i] * vector[i];
		}
		return res;
	}

	public double predictLabel(Sample s) {
		if (predict(s) >= 0) return 1;
		return 0;
	}

	public void runPerceptronLearning() {
		for (int iter = 0; iter < numIteration; iter ++) {
			double[][] vectors = new double[samples.size()][];
			double[] label = new double[samples.size()];
			double[] predictedLabel = new double[samples.size()];
			int incorrect = 0;
			for (int i=0; i<samples.size(); i++) {
				// System.out.println(i);
				vectors[i] = getFeatureVector(samples.get(i));
				label[i] = getLabel(samples.get(i));
				predictedLabel[i] = predictLabel(samples.get(i));
				if (Math.abs(label[i] - predictedLabel[i]) > 0.5) incorrect ++;
			}
			// System.out.println("Incorrect " + incorrect + " total: " + samples.size());
			for (int i=0; i<weights.length; i++) {
				double delta = 0;
				for (int j=0; j<samples.size(); j++) {
					// System.out.println(i + " " + j);
					double factor = 1;
					// if (label[j] > 0.5 && predictedLabel[j] < 0.5) factor = 3;
					delta += vectors[j][i] * (label[j] - predictedLabel[j]) * factor;
					// System.out.println(label[j] + " " + predictedLabel[j]);
					// weights[i] += learningRate * delta;
				}
				weights[i] += learningRate * delta;
				// if (className.equals("c1")) System.out.println("update w " +i + " "+ delta);
			}
		}
	}
}