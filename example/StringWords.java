package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

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

    /**
     *
     * 0.5 SEC
     * 273 WORDS
     *
     * CAN be optimised withh A / I check
     *
     *todo --Mistake: optimise substring word taking :)
     */
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


    public static void main(String[] args) {
        //array is already sroted, so binary search
        String[] allWords = loadAllWords().toArray(new String[0]);
        long start = System.nanoTime();
        Arrays.sort(allWords);
         List<String> nineLetterWords =  Arrays.stream(allWords).filter(it -> it.length() == 9).toList().stream().sorted().toList(); //List.of("STARLINK");
        allWordsBetween2and8Letters = Arrays.stream(allWords)
                .filter(it -> it.length() >= 1 && it.length() <= 9)
                .sorted()
                .toArray(String[]::new);

//        System.out.println(new DepthWord(mapChildren("TROUTLING", 0), "TROUTLING"));
//        System.out.println(new DepthWord(mapChildren("STARTLING", 0), "STARTLING"));
//        System.out.println(new DepthWord(mapChildren("STARTLING", 0), "STARTLING").getDepth());
//        System.out.println(new DepthWord(mapChildren("ABAMPERES", 0), "ABAMPERES"));
//        System.out.println(new DepthWord(mapChildren("ABAMPERES", 0), "ABAMPERES").getDepth());
        List<String> myWords = nineLetterWords.stream().map(word -> {
                    List<DepthWord> childWord = new LinkedList<>();
                    for (int i = 0; i < word.length(); i++) {
                        childWord.add(mapChildren(word, i));
                    }
                    return new DepthWord(childWord.stream().filter(Objects::nonNull).max(Comparator.comparingInt(DepthWord::getDepth)).orElse(null), word);
                })
                .filter(Objects::nonNull)
                .filter(it -> it.getDepth() == 9)
                .map(it -> it.word)
                .toList();

        System.out.println(myWords.size());

//         List<String> validWords = new LinkedList<>();
//        for (String word: nineLetterWords) {
////            System.out.println(word);
//            if(wordIsValidBruteForce(word, allWords)){
////                System.out.println("found ");
////                System.out.println(word);
//                validWords.add(word);
//            }
//        }
        System.out.printf("Time taken: %.2f seconds\n", (System.nanoTime() - start) / 1000000000.0);
//        System.out.printf("All Words Found are: %d\n\n", validWords.size());
//        System.out.println();
//        validWords.forEach(System.out::println);
//        for (String word:
//             loadAllWords()) {
//            System.out.println(word);
//        }
    }



    static class DepthWord{
        DepthWord childWord;
        String word;
//        int depth;
        public DepthWord(DepthWord childWord, String word) {
            this.childWord = childWord;
            this.word = word;
//            depth = getDepth();
        }

        public int getDepth(){
            int depth = 0;
            DepthWord current = this;
            while (current != null) {
                depth++;
                current = current.childWord;
            }
            return depth;
        }

        @Override
        public String toString() {
            return "DepthWord{" +
                    "childWord=" + childWord +
                    ", word='" + word + '\'' +
                    '}';
        }
    }
    private static String[] allWordsBetween2and8Letters;


    private static DepthWord mapChildren(String wordValue, int index){

        StringBuilder checkValue = new StringBuilder();
        for (int i = 0; i < wordValue.length(); i++) {
            if(i != index){
                checkValue.append(wordValue.charAt(i));
            }
        }
        if(checkValue.toString().length() == 1){
            if(checkValue.toString().equals("A") || checkValue.toString().equals("I")){
                return new DepthWord(null, checkValue.toString());
            }else{
                return null;
            }
        }
        if(Arrays.binarySearch(allWordsBetween2and8Letters, checkValue.toString()) >= 0){
            List<DepthWord> childWord = new LinkedList<>();
            for (int i = 0; i < checkValue.toString().length(); i++) {
                childWord.add(mapChildren(checkValue.toString(), i));
            }
            return new DepthWord(childWord.stream().filter(Objects::nonNull).max(Comparator.comparingInt(DepthWord::getDepth)).orElse(null), checkValue.toString());
        }else{
            if(index == wordValue.length()-1){
                return null;
            }
            return mapChildren(wordValue, ++index);
        }

    }


}
