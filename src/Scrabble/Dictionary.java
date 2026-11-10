package Scrabble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * Stores valid Scrabble words and prefixes used for word validation and solver searching
 */
public class Dictionary {
    private final HashSet<String> words;
    private final HashSet<String> prefixes;

    /**
     * Loads words and their prefixes from a dictionary file.
     * @param fileName the path to the dictionary file
     * @throws IOException if the dictionary file cannot be read
     */
    public Dictionary(String fileName) throws IOException{
        words = new HashSet<>();
        prefixes = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String word;
        while ((word = reader.readLine()) != null){
            word = word.toLowerCase();
            words.add(word);
            for (int i = 1; i < word.length(); i++){
                prefixes.add(word.substring(0, i));
            }
        }
        reader.close();
    }

    /**
     * Checks whether a word is in the dictionary
     * @param word the word to check
     * @return true if the word is valid
     */
    public boolean isWord(String word){
        return words.contains(word.toLowerCase());
    }

    /**
     * Checks whether a string is a prefix of a dictionary word
     * @param prefix the prefix to check
     * @return true if the prefix can begin a valid word
     */
    public boolean isPrefix(String prefix){
        return prefixes.contains(prefix.toLowerCase());
    }
}
