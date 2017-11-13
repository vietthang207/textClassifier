import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;

class Sample implements Serializable {

	String className;
	String fileLocation;
	String[] words;

	public Sample(String className, String fileLocation, HashSet<String> stopWords) throws IOException {
		this.className = className;
		this.fileLocation = fileLocation;
		try {
			words = Preprocessor.preprocessFile(fileLocation, stopWords);
		}
		catch (Exception e) {
			System.out.println("Read file error");
		}
	}
}