package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class StringWords {
    private static List<String> loadAllWords() throws IOException{
            URL wordsUrl = new URL("https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(wordsUrl.openConnection().getInputStream()))) {
                return bufferedReader.lines().skip(2).toList();
            }
    }

     // with optimisations 1 and 2 - result 775 words for ~ [0.65 - 0.8] seconds

    public static void main(String[] args) throws IOException {
        String[] allWords = loadAllWords().toArray(new String[0]);
        long start = System.nanoTime();
        Arrays.sort(allWords);
         List<String> nineLetterWords =  Arrays.stream(allWords).filter(it -> it.length() == 9).toList().stream().sorted().toList(); //List.of("STARTLING");
        allWordsBetween2and9LettersSet = Arrays.stream(allWords)
                .filter(it -> it.length() >= 2 && it.length() <= 9)
                .collect(Collectors.toSet());

        List<String> myWords = nineLetterWords.stream().map(word -> {
                    List<DepthWord> childWord = new LinkedList<>();
                    for (int i = 0; i < word.length(); i++) {
                        childWord.add(mapChildren(word, i));
                    }
                    return new DepthWord(childWord.stream().filter(Objects::nonNull).max(Comparator.comparingInt(DepthWord::getDepth)).orElse(null), word);
                })
                .filter(it -> it.getDepth() == 9)
                .map(it -> it.word)
                .toList();

        System.out.println(myWords.size());

        System.out.printf("Time taken: %.2f seconds\n", (System.nanoTime() - start) / 1000000000.0);
    }


    /**
     * Helper class for easier word in-depth traverse
     */
    static class DepthWord{
        DepthWord childWord;
        String word;
        public DepthWord(DepthWord childWord, String word) {
            this.childWord = childWord;
            this.word = word;
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

     //  private static String[] allWordsBetween2and9Letters; - speed of O(log(n)) for binary search
    //speed optimisation - HashSet - speed O(1) for search
    private static  Set<String> allWordsBetween2and9LettersSet = new HashSet<>();

    //speed optimisation 2 - caching already found words
    private static final Map<String, DepthWord> cache = new HashMap<>();


    private static DepthWord mapChildren(String wordValue, int index){

        String key = wordValue + "#" + index;
        if(cache.containsKey(key)){
            return cache.get(key);
        }

        StringBuilder reducedWord = new StringBuilder();
        for (int i = 0; i < wordValue.length(); i++) {
            if(i != index){
                reducedWord.append(wordValue.charAt(i));
            }
        }
        DepthWord value;
        if(reducedWord.toString().length() == 1){
            if(reducedWord.toString().equals("A") || reducedWord.toString().equals("I")){
                value = new DepthWord(null, reducedWord.toString());
            }else{
                value = null;
            }
        }else if(allWordsBetween2and9LettersSet.contains(reducedWord.toString())){
            List<DepthWord> childWord = new LinkedList<>();
            for (int i = 0; i < reducedWord.toString().length(); i++) {
                childWord.add(mapChildren(reducedWord.toString(), i));
            }
            value = new DepthWord(childWord.stream().filter(Objects::nonNull).max(Comparator.comparingInt(DepthWord::getDepth)).orElse(null), reducedWord.toString());
        }else{
            if(index == wordValue.length()-1){
                value = null;
            }else{
            value = mapChildren(wordValue, ++index);
            }
        }

        cache.put(key, value);

        return value;
    }


}
