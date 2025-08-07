package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class StringWords {
    /**
     * googled stuff:
     //https://www.geeksforgeeks.org/java/arrays-binarysearch-java-examples-set-1/
     //https://stackoverflow.com/questions/692569/how-can-i-count-the-time-it-takes-a-function-to-complete-in-java
     */
    private static List<String> loadAllWords() {
        try {
            URL wordsUrl = new URL("https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(wordsUrl.openConnection().getInputStream()))) {
                return bufferedReader.lines().skip(2).toList();
            }
        }catch (IOException ioEx){
            System.out.printf("ERROR loading words: %s", ioEx.getMessage());
            System.out.println(Arrays.toString(ioEx.getStackTrace()));
            throw new RuntimeException(ioEx); // todo add throwing exception after testing is done, this stays for faster testing 
        }
    }

    private static boolean wordIsValidBruteForce(String word, String[] allWords){
        int counter = 1;
//        System.out.println(word);
        for (int lettersToRemove = word.length() - 1; lettersToRemove >= 1; lettersToRemove--) {
            int leftSize = word.length() - lettersToRemove;
            for (int i = 0; i < leftSize; i++) {
              String wordToCheck = word.substring(i, i+ lettersToRemove);
//                System.out.println(wordToCheck + " ->> " + word);
              if(Arrays.binarySearch(allWords,wordToCheck) >= 0){
                  counter++;
                 // System.out.println(wordToCheck);
                      break;
              }
            }
            if(counter >= 8){
                break;
            }
        }
        return counter >= 8 ;
    }

    private static boolean wordIsValidBruteForce2(String word, String[] allWords){
        int counter = 1;
        for (int lettersToRemove = 1; lettersToRemove < word.length(); lettersToRemove++) {
            int leftSize = word.length() - lettersToRemove;
            for (int i = 0; i < leftSize; i++) {
                String wordToCheck = word.substring(i, i+ lettersToRemove);
                if(Arrays.binarySearch(allWords,wordToCheck) >= 0){
                    counter++;
                    // System.out.println(wordToCheck);
                    break;
                }
            }
            if(counter >= 8){
                break;
            }
        }
        return counter >= 8 ;
    }

    public static void main(String[] args) {
        //array is already sroted, so binary search
        String[] allWords = loadAllWords().toArray(new String[0]);
        long start = System.nanoTime();
        Arrays.sort(allWords);
         List<String> nineLetterWords = Arrays.stream(allWords).filter(it -> it.length() == 9).toList().stream().sorted().toList(); //List.of("STARLINK");
         List<String> validWords = new LinkedList<>();
        for (String word: nineLetterWords) {
//            System.out.println(word);
            if(wordIsValidBruteForce(word, allWords)){
//                System.out.println("found ");
//                System.out.println(word);
                validWords.add(word);
            }
        }
        System.out.printf("Time taken: %.2f seconds\n", (System.nanoTime() - start) / 1000000000.0);
        System.out.printf("All Words Found are: %d\n\n", validWords.size());
        System.out.println();
        validWords.forEach(System.out::println);
//        for (String word:
//             loadAllWords()) {
//            System.out.println(word);
//        }
    }

}
