package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private String answer;
    private int steps;
    private int maxSteps = 6;
    private WordleDictionary dictionary;
    private PrintWriter log;
    private List<String> guesses;
    private List<String> results;
    private boolean win = false;

    // Для фильтрации подсказок
    private StringBuilder pattern; // Паттерн правильных букв (_ для неизвестных)
    private Set<Character> correctLetters; // Буквы, которые точно есть
    private Set<Character> wrongLetters; // Буквы, которых точно нет
    private Map<Character, Set<Integer>> wrongPositions; // Буквы и позиции, где их нет

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.steps = 0;
        this.guesses = new ArrayList<>();
        this.results = new ArrayList<>();

        // Инициализация структур для подсказок
        this.pattern = new StringBuilder("_____");
        this.correctLetters = new HashSet<>();
        this.wrongLetters = new HashSet<>();
        this.wrongPositions = new HashMap<>();

        log.println("Загадано слово: " + answer);
    }

    public String makeGuess(String guess) throws GameException {
        // Нормализуем слово
        String normalizedGuess = WordleDictionary.normalizeWord(guess);

        // Проверяем валидность
        if (!WordleDictionary.isValidWord(normalizedGuess)) {
            throw new GameException("Слово должно состоять из 5 русских букв");
        }

        // Проверяем наличие в словаре
        if (!dictionary.contains(normalizedGuess)) {
            throw new GameException("Слово не найдено в словаре");
        }

        // Проверяем, не закончились ли попытки
        if (isGameOver()) {
            throw new GameException("Игра уже окончена");
        }

        // Проверяем результат
        String result = WordleDictionary.compareWords(normalizedGuess, answer);

        // Сохраняем ход
        guesses.add(normalizedGuess);
        results.add(result);
        steps++;

        log.println("Ход " + steps + ": игрок ввел '" + normalizedGuess + "', результат: " + result);

        // Обновляем информацию для подсказок
        updateHintInfo(normalizedGuess, result);

        // Проверяем победу
        if (normalizedGuess.equals(answer)) {
            win = true;
            log.println("Игрок выиграл!");
        } else if (steps >= maxSteps) {
            log.println("Игрок проиграл, закончились попытки");
        }

        return result;
    }

    private void updateHintInfo(String guess, String result) {
        for (int i = 0; i < result.length(); i++) {
            char resultChar = result.charAt(i);
            char guessChar = guess.charAt(i);

            if (resultChar == '+') {
                // Буква на правильном месте
                pattern.setCharAt(i, guessChar);
                correctLetters.add(guessChar);
            } else if (resultChar == '^') {
                // Буква есть, но не на этом месте
                correctLetters.add(guessChar);
                wrongPositions.computeIfAbsent(guessChar, k -> new HashSet<>()).add(i);
            } else if (resultChar == '-') {
                // Буквы нет в слове
                // Добавляем в wrongLetters только если эта буква не встречается в correctLetters
                if (!correctLetters.contains(guessChar)) {
                    wrongLetters.add(guessChar);
                }
            }
        }
    }

    public String getHint() throws GameException {
        if (isGameOver()) {
            throw new GameException("Игра уже окончена");
        }

        // Преобразуем структуры данных в строки для фильтрации
        String patternStr = pattern.toString();

        StringBuilder correctLettersStr = new StringBuilder();
        for (char c : correctLetters) {
            correctLettersStr.append(c);
        }

        StringBuilder wrongLettersStr = new StringBuilder();
        for (char c : wrongLetters) {
            wrongLettersStr.append(c);
        }

        StringBuilder wrongPositionsStr = new StringBuilder();
        for (Map.Entry<Character, Set<Integer>> entry : wrongPositions.entrySet()) {
            wrongPositionsStr.append(entry.getKey());
            for (int pos : entry.getValue()) {
                wrongPositionsStr.append(pos);
            }
            wrongPositionsStr.append(";");
        }

        // Фильтруем слова
        List<String> possibleWords = dictionary.filterWords(
                patternStr,
                correctLettersStr.toString(),
                wrongLettersStr.toString(),
                wrongPositionsStr.toString()
        );

        // Убираем уже угаданные слова
        possibleWords.removeAll(guesses);

        if (possibleWords.isEmpty()) {
            throw new GameException("Не найдено подходящих слов");
        }

        // Выбираем случайное слово из возможных
        Random random = new Random();
        String hint = possibleWords.get(random.nextInt(possibleWords.size()));

        log.println("Выдана подсказка: " + hint + " (из " + possibleWords.size() + " возможных слов)");

        return hint;
    }

    public int getStep() {
        return steps;
    }

    public boolean isGameOver() {
        return win || steps >= maxSteps;
    }

    public boolean isWin() {
        return win;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getGuesses() {
        return Collections.unmodifiableList(guesses);
    }

    public List<String> getResults() {
        return Collections.unmodifiableList(results);
    }
}
