
class Hyperparameters {
	double chiSquareThreshold;
	int kThreshold;
	double learningRate;
	int numIteration;

	public Hyperparameters(double chiSquareThreshold, int kThreshold, double learningRate, int numIteration) {
		this.chiSquareThreshold = chiSquareThreshold;
		this.kThreshold = kThreshold;
		this.learningRate = learningRate;
		this.numIteration = numIteration;
	}

	public String toString() {
		String res = "";
		res = res + chiSquareThreshold + " " + kThreshold + " " + learningRate + " " + numIteration;
		return res;
	}
}