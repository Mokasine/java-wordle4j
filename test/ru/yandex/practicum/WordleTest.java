package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {

    @Test
    void testNormalizeWord() {
        // Простая проверка нормализации
        assertEquals("стол", WordleDictionary.normalizeWord("СТОЛ"));
        assertEquals("ежик", WordleDictionary.normalizeWord("ёжик"));
    }

    @Test
    void testIsValidWord() {
        // Проверяем валидные слова
        assertTrue(WordleDictionary.isValidWord("стол"));
        assertTrue(WordleDictionary.isValidWord("стул"));

        // Проверяем невалидные слова
        assertFalse(WordleDictionary.isValidWord("ст"));
        assertFalse(WordleDictionary.isValidWord("столстол"));
    }

    @Test
    void testCompareWords() {
        // Одинаковые слова
        assertEquals("+++++", WordleDictionary.compareWords("стол", "стол"));

        // Разные слова
        assertEquals("^----", WordleDictionary.compareWords("стул", "стол"));
        assertEquals("+----", WordleDictionary.compareWords("слон", "стул"));
    }

    @Test
    void testDictionary() {
        // Создаем простой словарь
        List<String> words = Arrays.asList("стол", "стул", "окно");
        WordleDictionary dict = new WordleDictionary(words);

        // Проверяем основные методы
        assertFalse(dict.isEmpty());
        assertEquals(3, dict.size());
        assertTrue(dict.contains("стол"));
        assertFalse(dict.contains("дверь"));

        // Проверяем случайное слово
        String randomWord = dict.getRandomWord();
        assertNotNull(randomWord);
        assertTrue(words.contains(randomWord));
    }

    @Test
    void testGameInitialization() {
        // Подготавливаем данные
        List<String> words = Arrays.asList("стол", "стул");
        WordleDictionary dict = new WordleDictionary(words);

        // Создаем StringWriter для лога
        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        // Создаем игру
        WordleGame game = new WordleGame(dict, testLog);

        // Проверяем начальное состояние
        assertNotNull(game.getAnswer());
        assertEquals(0, game.getStep());
        assertFalse(game.isGameOver());
        assertFalse(game.isWin());
    }

    @Test
    void testMakeGuess() throws GameException {
        // Подготавливаем данные
        List<String> words = Arrays.asList("стол", "стул");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Делаем ход
        String result = game.makeGuess("стул");

        // Проверяем результат
        assertNotNull(result);
        assertEquals(1, game.getStep());
        assertTrue(game.getGuesses().contains("стул"));
        assertFalse(game.isGameOver());
    }

    @Test
    void testInvalidGuessThrowsException() {
        List<String> words = Arrays.asList("стол", "стул");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Слово не из словаря должно вызывать исключение
        assertThrows(GameException.class, () -> {
            game.makeGuess("абвгд");
        });
    }

    @Test
    void testWinCondition() throws GameException {
        List<String> words = Arrays.asList("стол", "стул");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        // Создаем игру и принудительно задаем ответ
        WordleGame game = new WordleGame(dict, testLog) {
            @Override
            public String getAnswer() {
                return "стол";
            }
        };

        // Угадываем правильное слово
        game.makeGuess("стол");

        // Проверяем победу
        assertTrue(game.isWin());
        assertTrue(game.isGameOver());
        assertEquals(1, game.getStep());
    }

    @Test
    void testGetHint() throws GameException {
        List<String> words = Arrays.asList("стол", "стул", "окно", "дверь");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Запрашиваем подсказку
        String hint = game.getHint();

        // Проверяем подсказку
        assertNotNull(hint);
        assertTrue(dict.contains(hint));
        assertEquals(5, hint.length()); // Слово должно быть из 5 букв
    }

    @Test
    void testMaxAttempts() throws GameException {
        List<String> words = Arrays.asList("стол", "стул", "окно");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Используем неправильное слово для всех попыток
        String wrongWord = "окно";

        // Делаем 6 попыток
        for (int i = 0; i < 6; i++) {
            game.makeGuess(wrongWord);
        }

        // Проверяем, что игра закончилась
        assertTrue(game.isGameOver());
        assertFalse(game.isWin()); // Мы не угадали
        assertEquals(6, game.getStep());
    }

    @Test
    void testGameExceptionMessage() {
        // Проверяем создание исключения
        GameException exception = new GameException("Тестовая ошибка");
        assertEquals("Тестовая ошибка", exception.getMessage());

        // Проверяем исключение с причиной
        Exception cause = new RuntimeException("Причина");
        GameException exceptionWithCause = new GameException("Ошибка", cause);
        assertEquals("Ошибка", exceptionWithCause.getMessage());
        assertEquals(cause, exceptionWithCause.getCause());
    }

    @Test
    void testEmptyDictionary() {
        // Создаем пустой словарь
        List<String> emptyList = Arrays.asList();
        WordleDictionary emptyDict = new WordleDictionary(emptyList);

        // Проверяем пустой словарь
        assertTrue(emptyDict.isEmpty());
        assertEquals(0, emptyDict.size());

        // Должно быть исключение при попытке получить случайное слово
        assertThrows(IllegalStateException.class, emptyDict::getRandomWord);
    }

    @Test
    void testCompareWordsDifferentLength() {
        // Проверяем сравнение слов разной длины
        assertThrows(IllegalArgumentException.class, () -> {
            WordleDictionary.compareWords("короткое", "длинное");
        });
    }

    @Test
    void testFilterWords() {
        // Создаем тестовый словарь
        List<String> words = Arrays.asList("стол", "стул", "окно", "дверь", "полка");
        WordleDictionary dict = new WordleDictionary(words);

        // Фильтруем слова, начинающиеся на "ст"
        List<String> filtered = dict.filterWords("ст__", "", "", "");

        // Проверяем результат
        assertTrue(filtered.contains("стол"));
        assertTrue(filtered.contains("стул"));
        assertEquals(2, filtered.size());
    }
}