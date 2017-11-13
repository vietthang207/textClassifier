import java.io.IOException;
import java.util.HashSet;

class Sample {

	String className;
	String fileLocation;
	String[] words;

	public Sample(String className, String fileLocation, HashSet<String> stopWords) throws IOException {
		this.className = className;
		this.fileLocation = fileLocation;
		words = Preprocessor.preprocessFile(fileLocation, stopWords);
	}
}