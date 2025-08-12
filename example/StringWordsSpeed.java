package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class StringWordsSpeed {

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

    private static boolean wordIsValid(String word, Set<String> allWords){

        if((word.equals("A") || word.equals("I"))){
            return true;
        }else if(!allWords.contains(word)){
            return false;
        }else{
            for (int i = 0; i < word.length(); i++) {
                if(wordIsValid( word.substring(0,i) + word.substring(i + 1),allWords)){
//                    System.out.println(word + " -> " + word.substring(0,i) + word.substring(i + 1) );
                    return true;
                }
            }

            return  false;
        }
    }


    public static void main(String[] args) {
        //array is already sroted, so binary search
        List<String> allWords = loadAllWords();
        long start = System.currentTimeMillis();
        List<String> nineLetterWords = allWords.stream().filter(it -> it.length() == 9).toList(); //List.of("STARTLING");
        Set<String> hashSet = new HashSet<>(allWords);
        int counter = 0;
        for (String word:
                nineLetterWords) {
            if(wordIsValid(word, hashSet)){
//                System.out.println(word);
                counter++;
            }
        }
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(counter + " words");
        System.out.println(timeElapsed + " ms");

    }

}
