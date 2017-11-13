import java.io.Serializable;

class Model implements Serializable{
	String[] classNames;
	Classifier[] classifiers;
	double chiSquareThreshold;
	int kThreshold;
	double learningRate;
	int numIteration;
	
	public Model(String[] classNames, double chiSquareThreshold, int kThreshold, double learningRate, int numIteration){
		this.classNames = classNames;
		this.chiSquareThreshold = chiSquareThreshold;
		this.kThreshold = kThreshold;
		this.learningRate = learningRate;
		this.numIteration = numIteration;
		classifiers = new Classifier[classNames.length];
		for (int i=0; i<classNames.length; i++) {
			classifiers[i] = new Classifier(classNames[i], chiSquareThreshold, kThreshold, learningRate, numIteration);
		}
	}

	public void addSample(Sample s) {
		// if (s==null || s.words==null) {
		// 	System.out.println("bla");
		// }
		for (int i=0; i<classNames.length; i++) {
			classifiers[i].addSample(s);
		}
	}

	public void train() {
		for (int i=0; i<classNames.length; i++) {
			classifiers[i].runFeatureSelection();
			classifiers[i].initializeWeights();
			classifiers[i].runPerceptronLearning();
		}
	}

	public String predict(Sample s) {
		double[] prediction = new double[classNames.length];
		for (int i=0; i<classNames.length; i++) {
			prediction[i] = classifiers[i].predict(s);
		}
		double res = Double.NEGATIVE_INFINITY;
		int mini = -1;
		for (int i=0; i<classNames.length; i++) {
			// System.out.print(prediction[i] + " ");
			if (prediction[i] > res) {
				mini = i;
				res = prediction[i];
			}
		}
		// System.out.println();
		// System.out.println(mini + " " + prediction[mini]);
		return classNames[mini];
	}

}