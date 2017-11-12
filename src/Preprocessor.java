import java.io.IOException;
import java.util.HashSet;

class Preprocessor {

	public static String normalizeLine(String line) {
		String res = "";
		char[] chars = line.toCharArray();
		for (char c: chars) {
			if (c == '\'') {
				// continue;
			} else if (Character.isLetter(c)) {
				res += Character.toLowerCase(c);
			} else {
				res += " ";
			}
		}
		return res;
	}

	public static String stem(String word) {
		Stemmer st = new Stemmer();
		char[] chars = word.toCharArray();
		for (char c: chars) {
			st.add(c);
		}
		st.stem();
		return st.toString();
	}

	public static HashSet<String> getStopWordFromFile(String fileName) throws IOException {
		HashSet<String> res = new HashSet<String>();
		DataReader dataReader = new DataReader(fileName);
		String line;
		while ( (line = dataReader.nextLine()) != null) {
			res.add(line.trim());
		}
		dataReader.close();
		return res;
	}

	public static String removeStopWords(String line, HashSet<String> stopWords) {
		String[] words = line.trim().split(" ");
		String res = "";
		for (String w: words) {
			if (!stopWords.contains(w)) {
				res += w + " ";
			}
		}
		return res;
	}

	public static String[] preprocessLine(String line, HashSet<String> stopWords) {
		String[] words = removeStopWords(normalizeLine(line), stopWords).split("\\s+");
		for (int i=0; i<words.length; i++) {
			words[i] = stem(words[i]);
		}
		return words;
	}

	public static String[] preprocessFile(String fileName, HashSet<String> stopWords) throws IOException {
		DataReader dataReader = new DataReader(fileName);
		String line;
		String str = "";
		while ( (line = dataReader.nextLine()) != null) {
			str += line + " ";
		}
		dataReader.close();
		return preprocessLine(str, stopWords);
	}
}