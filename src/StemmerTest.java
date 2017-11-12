import java.io.IOException;

class StemmerTest {
	public static void main(String[] args) throws IOException {
		Stemmer st = new Stemmer();
		String str = args[0];
		str = "affiliations";
		char[] chars = str.toCharArray();
		for (char c: chars) {
			st.add(c);
		}
		st.stem();
		String res = st.toString();
		System.out.println(res);
		String[] words = Preprocessor.preprocessFile("../tc/c1/37261", Preprocessor.getStopWordFromFile("stopword-list"));
		for (String w: words) {
			System.out.print(w + " ");
		}
		System.out.println(words.length);
	}
}