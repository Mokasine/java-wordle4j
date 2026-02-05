package ru.yandex.practicum;

import java.util.*;

public class WordleDictionary {
    private List<String> words;
    private Random random;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
        this.random = new Random();
    }

    public List<String> getWords() {
        return Collections.unmodifiableList(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public int size() {
        return words.size();
    }

    public String getRandomWord() {
        if (isEmpty()) {
            throw new IllegalStateException("Словарь пуст");
        }
        return words.get(random.nextInt(words.size()));
    }

    public List<String> filterWords(String pattern, String correctLetters, String wrongLetters,
                                    String wrongPositions) {
        List<String> result = new ArrayList<>();

        for (String word : words) {
            if (matchesPattern(word, pattern, correctLetters, wrongLetters, wrongPositions)) {
                result.add(word);
            }
        }

        return result;
    }

    private boolean matchesPattern(String word, String pattern, String correctLetters,
                                   String wrongLetters, String wrongPositions) {
        // Проверяем паттерн (буквы на конкретных позициях)
        if (pattern != null && !pattern.isEmpty()) {
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                if (c != '_' && word.charAt(i) != c) {
                    return false;
                }
            }
        }

        // Проверяем обязательные буквы
        if (correctLetters != null && !correctLetters.isEmpty()) {
            for (char c : correctLetters.toCharArray()) {
                if (word.indexOf(c) == -1) {
                    return false;
                }
            }
        }

        // Проверяем буквы, которых не должно быть
        if (wrongLetters != null && !wrongLetters.isEmpty()) {
            for (char c : wrongLetters.toCharArray()) {
                if (word.indexOf(c) != -1) {
                    return false;
                }
            }
        }

        // Проверяем буквы, которые есть, но не на этих позициях
        if (wrongPositions != null && !wrongPositions.isEmpty()) {
            String[] positions = wrongPositions.split(";");
            for (String pos : positions) {
                if (pos.length() < 2) continue;
                char letter = pos.charAt(0);
                String positionStr = pos.substring(1);

                // Проверяем, что буква есть в слове
                if (word.indexOf(letter) == -1) {
                    return false;
                }

                // Проверяем, что буква не на указанных позициях
                for (char posChar : positionStr.toCharArray()) {
                    int position = Character.getNumericValue(posChar);
                    if (position < word.length() && word.charAt(position) == letter) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static String compareWords(String guess, String answer) {
        if (guess.length() != answer.length()) {
            throw new IllegalArgumentException("Слова должны быть одинаковой длины");
        }

        StringBuilder result = new StringBuilder();
        Map<Character, Integer> answerLetterCount = new HashMap<>();

        // Сначала считаем буквы в ответе
        for (char c : answer.toCharArray()) {
            answerLetterCount.put(c, answerLetterCount.getOrDefault(c, 0) + 1);
        }

        // Проходим первый раз для нахождения точных совпадений
        char[] guessChars = guess.toCharArray();
        char[] answerChars = answer.toCharArray();
        char[] resultChars = new char[guess.length()];

        // Заполняем массив результата
        for (int i = 0; i < guessChars.length; i++) {
            resultChars[i] = '-';
        }

        // Первый проход: точные совпадения
        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == answerChars[i]) {
                resultChars[i] = '+';
                answerLetterCount.put(guessChars[i], answerLetterCount.get(guessChars[i]) - 1);
            }
        }

        // Второй проход: частичные совпадения
        for (int i = 0; i < guessChars.length; i++) {
            if (resultChars[i] == '+') continue;

            char c = guessChars[i];
            if (answerLetterCount.containsKey(c) && answerLetterCount.get(c) > 0) {
                resultChars[i] = '^';
                answerLetterCount.put(c, answerLetterCount.get(c) - 1);
            }
        }

        return new String(resultChars);
    }

    public static String normalizeWord(String word) {
        if (word == null) return "";

        String normalized = word.toLowerCase()
                .replace('ё', 'е')
                .replace('ъ', 'ь')
                .trim();

        // Убираем все не-буквы
        StringBuilder result = new StringBuilder();
        for (char c : normalized.toCharArray()) {
            if (Character.isLetter(c) && c >= 'а' && c <= 'я') {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static boolean isValidWord(String word) {
        if (word == null || word.length() != 5) {
            return false;
        }

        for (char c : word.toCharArray()) {
            if (!Character.isLetter(c) || c < 'а' || c > 'я') {
                return false;
            }
        }

        return true;
    }
}