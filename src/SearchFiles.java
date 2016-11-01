
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

/** Simple command-line based search demo. */
public class SearchFiles {

	private static ArrayList<String> stopWords;
	private static ArrayList<String> userAddedstopWords;

	private SearchFiles() {
	}

	/** Simple command-line based search demo. */
	public static void main(String[] args) throws Exception {

		initialiseArrays();

		String usage = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/java/4_0/demo.html for details.";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		String index = "index";
		String field = "contents";
		String queries = null;
		int repeat = 0;
		boolean raw = false;
		String queryString = null;
		int hitsPerPage = 10;

		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				index = args[i + 1];
				i++;
			} else if ("-field".equals(args[i])) {
				field = args[i + 1];
				i++;
			} else if ("-queries".equals(args[i])) {
				queries = args[i + 1];
				i++;
			} else if ("-query".equals(args[i])) {
				queryString = args[i + 1];
				i++;
			} else if ("-repeat".equals(args[i])) {
				repeat = Integer.parseInt(args[i + 1]);
				i++;
			} else if ("-raw".equals(args[i])) {
				raw = true;
			} else if ("-paging".equals(args[i])) {
				hitsPerPage = Integer.parseInt(args[i + 1]);
				if (hitsPerPage <= 0) {
					System.err.println("There must be at least 1 hit per page.");
					System.exit(1);
				}
				i++;
			}
		}

		IndexReader reader = DirectoryReader.open(FSDirectory.open(FileSystems.getDefault().getPath(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();

		BufferedReader in = null;
		if (queries != null) {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(queries), "UTF-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		}

		QueryParser parser = new QueryParser(field, analyzer);
		while (true) {
			if (queries == null && queryString == null) { // prompt the user
				System.out.println("Enter query: ");
			}

			String line = queryString != null ? queryString : in.readLine();

			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim();
			if (line.length() == 0) {
				break;
			}

			Query query = parser.parse(line);
			System.out.println("Searching for: " + query.toString(field));

			if (repeat > 0) { // repeat & time as benchmark
				Date start = new Date();
				for (int i = 0; i < repeat; i++) {
					searcher.search(query, 100);
				}
				Date end = new Date();
				System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
			}

			doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null, line);

			if (queryString != null) {
				break;
			}
		}
		reader.close();
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search
	 * engine presents pages of size n to the user. The user can then go to the
	 * next page if interested in the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results
	 * are collected to fill 5 result pages. If the user wants to page beyond
	 * this limit, then the query is executed another time and all hits are
	 * collected.
	 * 
	 * @throws Exception
	 * 
	 */
	public static void addingStopWords(String input) {

		for (String word : input.split(" ")) {
			userAddedstopWords.add(word);
		}
	}

	public static void initialiseArrays() throws IOException {
		userAddedstopWords = new ArrayList<String>();

		BufferedReader br = null;
		stopWords = new ArrayList<String>();
		String sCurrentLine = null;
		Boolean compared = false;

		br = new BufferedReader(new FileReader("ListOfStopWords.txt"));
		stopWords.add(sCurrentLine);

		while ((sCurrentLine = br.readLine()) != null) {

			stopWords.add(sCurrentLine);

		}
	}

	public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage,
			boolean raw, boolean interactive, String line1) throws Exception {

		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;
		int numTotalHits = results.totalHits;
		System.out.println("this is the quesry " + line1);
		System.out.println(numTotalHits + " total matching documents");

		int start = 0;
		int end = Math.min(numTotalHits, hitsPerPage);

		while (true) {
			if (end > hits.length) {
				System.out.println("Only results 1 - " + hits.length + " of " + numTotalHits
						+ " total matching documents collected.");
				System.out.println("Collect more (y/n) ?");
				String line = in.readLine();
				if (line.length() == 0 || line.charAt(0) == 'n') {
					break;
				}

				hits = searcher.search(query, numTotalHits).scoreDocs;
			}

			end = Math.min(hits.length, start + hitsPerPage);

			for (int i = start; i < end; i++) {
				if (raw) { // output raw format
					System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				if (path != null) {
					System.out.println((i + 1) + ". " + path + tokenisingTheUserInput(line1, path, false)); // default
																											// not
																											// to
																											// remove
																											// duplicates

					String title = doc.get("title");
					if (title != null) {
						System.out.println("   Title: " + doc.get("title"));
					}
				} else {
					System.out.println((i + 1) + ". " + "No path for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

			if (numTotalHits >= end) {
				boolean quit = false;
				while (true) {
					System.out.print("Press ");
					if (start - hitsPerPage >= 0) {
						System.out.print("(p)revious page, ");
					}
					if (start + hitsPerPage < numTotalHits) {
						System.out.print("(n)ext page, ");
					}
					System.out.println("(q)uit or enter number to jump to a page.");

					String line = in.readLine();
					if (line.length() == 0 || line.charAt(0) == 'q') {
						quit = true;
						break;
					}
					if (line.charAt(0) == 'p') {
						start = Math.max(0, start - hitsPerPage);
						break;
					} else if (line.charAt(0) == 'n') {
						if (start + hitsPerPage < numTotalHits) {
							start += hitsPerPage;
						}
						break;
					} else {
						int page = Integer.parseInt(line);
						if ((page - 1) * hitsPerPage < numTotalHits) {
							start = (page - 1) * hitsPerPage;
							break;
						} else {
							System.out.println("No such page");
						}
					}
				}
				if (quit)
					break;
				end = Math.min(numTotalHits, start + hitsPerPage);
			}
		}
	}

	public static String tokenisingTheUserInput(String query, String path, boolean removeDuplicates) throws Exception {

		ArrayList<String> wordArrayList = new ArrayList<String>();
		for (String word : query.split(" ")) {
			if (!stopWords.contains(word)) {
				if (!userAddedstopWords.contains(word)) {
					wordArrayList.add(word);
				}
			}
		}

		String trueQuery = "";
		for (int i = 0; i < wordArrayList.size(); i++) {
			trueQuery += wordArrayList.get(i) + " ";
		}

		// System.out.println("true query = " + trueQuery);

		StringTokenizer defaultTokenizer = new StringTokenizer(trueQuery);
		int size = defaultTokenizer.countTokens();

		// create array and then set the values
		String[] tokenOfInput = new String[size];
		for (int t = 0; t < tokenOfInput.length; t++) {
			tokenOfInput[t] = defaultTokenizer.nextToken();
		}

		// creating 2d array and then set values
		String[][] table = new String[size][2];
		for (int p = 0; p < table.length; p++) {
			for (int j = 0; j < table[p].length; j++) {
				table[p][j] = "0";
			}
		}

		for (int i = 0; i < table.length; i++) {
			searchingThroughDocForWordHits(tokenOfInput[i], path, size, table, i);
		}
		// System.out.print("The ammount of times the word appear in the
		// document for the document above " + (Arrays.deepToString(table)));
		if (removeDuplicates) {
			cleanTheArray(table);
		}
		String wordHits = Arrays.deepToString(table);
		
		String nullsRemoved = removingNulls(wordHits);
		
		String wordHitsWithPath = "The path is: " + path + " . The words hit are:" + nullsRemoved;
		return wordHitsWithPath;
	}

	private static String removingNulls(String wordHits) {
		
		String content = wordHits.replace("[null, 0],", "");
		
		return content;
	};

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

	private static String[][] searchingThroughDocForWordHits(String queryWord, String path, int size, String[][] table,
			int i) throws FileNotFoundException {

		Scanner in = new Scanner(new File(path));

		if((queryWord.charAt(queryWord.length() - 1) != 'y')){
			if((queryWord.charAt(queryWord.length() - 1) != 'e')){
			PorterStemmer stemmer = new PorterStemmer();
			stemmer.setCurrent(queryWord);
			stemmer.stem();
			queryWord = stemmer.getCurrent();
		}
	}
		

		// System.out.println("this is resulting from queryWord = " +
		// queryWord); // TODO: this needs fixed, some words ending in 'y' get
		// screwed up. (funny -> funni)

		while (in.hasNext()) {
			String s = in.next(); // get the next token in the file
			table[i][0] = queryWord;
			if (queryWord.equals(s.toString())) {

				// table[i][0] = queryWord;
				int count = Integer.parseInt(table[i][1]);

				count++;
				table[i][1] = String.valueOf(count);

			}
		}

		in.close(); // close the scanner

		return table;

	}

	private static boolean doesNotHitAStopWord(String queryWord) {
		for (int i = 0; i < stopWords.size(); i++) {
			if (queryWord.equals(stopWords.get(i)));
			System.out.println("you hit a stop word dude---" + queryWord + "" + stopWords.get(i));
			return false;
		}

		return true;
	}

	public static String doAdvancedSearch(String allWords, String exactWords, String anyWords, String noneWords,
			int fromRange, int toRange, boolean removeDuplicates) {
		return "";
	}

}
