import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.ext.PorterStemmer;

public class XMLObject {

	    public String name;
	    public String overview;
	    public String text;
	    public String firstTenWordsOfTextTag;
	    public String path;
	    public ArrayList<String> query;
	    public String trueQuery;
	    public boolean removeDuplicates;
	   
	    //constructor
	    public XMLObject(String inputName, String inputOverview, String inputText, String inputPath,ArrayList<String> wordArrayList, String inputTrueQuery, boolean inputRemoveDuplicates) throws FileNotFoundException {
	        this.name = inputName;
	        this.overview = inputOverview;
	        this.text = inputText;
	        this.firstTenWordsOfTextTag = getFirst10Words(inputText);
	        this.path = inputPath;
	        this.query = wordArrayList;
	        
			StringTokenizer defaultTokenizer = new StringTokenizer(trueQuery);
			int size = defaultTokenizer.countTokens();

			// create array and then set the values
			String[] tokenOfInput = new String[size];
			for (int t = 0; t < tokenOfInput.length; t++) {
				tokenOfInput[t] = defaultTokenizer.nextToken();
				// System.out.println("11111111");
			}

			// creating 2d array and then set values
			String[][] table = new String[size][2];
			for (int p = 0; p < table.length; p++) {
				for (int j = 0; j < table[p].length; j++) {
					table[p][j] = "0";
					// System.out.println("11111111");
				}
			}
			
			for (int i = 0; i < table.length; i++) {
				
				searchingThroughDocForWordHits(tokenOfInput[i], text, table, i);
			}
			
			if (removeDuplicates) {
				cleanTheArray(table);
			}
	        
	    }
	    private static String[][] searchingThroughDocForWordHits(String queryWord, String text, String[][] table,
				int i) throws FileNotFoundException {

			if (!(queryWord.endsWith("y") || queryWord.endsWith("e"))) {
				PorterStemmer stemmer = new PorterStemmer();
				stemmer.setCurrent(queryWord);
				stemmer.stem();
				queryWord = stemmer.getCurrent();
			}
			
			ArrayList<String> textOfWordsArray = new ArrayList<String>();
			for (String word : text.split(" ")) {
						textOfWordsArray.add(word);
					}
				
			for(i=0; i<textOfWordsArray.size();i++){
				table[i][0] = queryWord;
				if (queryWord.equals(textOfWordsArray.get(i))) {

					//table[i][0] = queryWord;
					int count = Integer.parseInt(table[i][1]);

					count++;
					table[i][1] = String.valueOf(count);

				}
			}

			
//			while (in.hasNext()) {
//				String s = in.next(); // get the next token in the file
//				table[i][0] = queryWord;
//				if (queryWord.equals(s.toString())) {
//
//					// table[i][0] = queryWord;
//					int count = Integer.parseInt(table[i][1]);
//
//					count++;
//					table[i][1] = String.valueOf(count);
//
//				}
//			}

			return table;

		}
	    

	    public String getFirst10Words(String arg) {
	        Pattern pattern = Pattern.compile("([\\S]+\\s*){1,10}");
	        Matcher matcher = pattern.matcher(arg);
	        matcher.find();
	        return matcher.group();
	    }
	    
		public static String[][] cleanTheArray(String[][] table) {

			for (int p = 0; p < table.length; p++) {
				int countOfDuplicate = 0;
				for (int j = 0; j < table.length; j++) {

					String stringAgainstArray = table[p][0];
					String stringFromArray = table[j][0];

					if (stringAgainstArray.equals(stringFromArray)) {

						countOfDuplicate++;
						if (countOfDuplicate == 2) {
							table[p][0] = "null";
							table[p][1] = "0";
						}

					}
				}

			}
			return table;
		}
		
}

