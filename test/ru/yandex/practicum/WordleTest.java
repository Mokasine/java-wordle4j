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
        // Проверяем нормализацию слов
        assertEquals("столы", WordleDictionary.normalizeWord("СТОЛЫ"));
        assertEquals("ежики", WordleDictionary.normalizeWord("ёжики"));
        assertEquals("сьемка", WordleDictionary.normalizeWord("съёмка"));
        assertEquals("обьем", WordleDictionary.normalizeWord("объём"));
    }

    @Test
    void testIsValidWord() {
        // Проверяем валидацию 5-буквенных слов
        assertTrue(WordleDictionary.isValidWord("столы"));
        assertTrue(WordleDictionary.isValidWord("книга"));
        assertTrue(WordleDictionary.isValidWord("ручка"));

        // Проверяем невалидные слова
        assertFalse(WordleDictionary.isValidWord("окно")); // 4 буквы
        assertFalse(WordleDictionary.isValidWord("ст")); // слишком короткое
        assertFalse(WordleDictionary.isValidWord("столовая")); // слишком длинное
        assertFalse(WordleDictionary.isValidWord("table")); // английские буквы
    }

    @Test
    void testCompareWords() {
        // Проверяем сравнение слов по правилам Wordle
        assertEquals("+++++", WordleDictionary.compareWords("книга", "книга")); // все буквы совпали
        assertEquals("---^+", WordleDictionary.compareWords("ручка", "книга")); // первая буква на месте
        assertEquals("----^", WordleDictionary.compareWords("ножик", "ручка")); // вторая буква на месте
    }

    @Test
    void testDictionary() {
        // Проверяем работу словаря
        List<String> words = Arrays.asList("книга", "ручка", "чашка", "ложка", "вилка");
        WordleDictionary dict = new WordleDictionary(words);

        assertFalse(dict.isEmpty());
        assertEquals(5, dict.size());
        assertTrue(dict.contains("книга"));
        assertFalse(dict.contains("компьютер"));

        // Проверяем получение случайного слова
        String randomWord = dict.getRandomWord();
        assertNotNull(randomWord);
        assertTrue(words.contains(randomWord));
    }

    @Test
    void testGameInitialization() {
        // Проверяем создание игры
        List<String> words = Arrays.asList("книга", "ручка", "чашка", "ложка");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Проверяем начальное состояние
        assertNotNull(game.getAnswer());
        assertEquals(5, game.getAnswer().length()); // Слово должно быть из 5 букв
        assertEquals(0, game.getStep());
        assertFalse(game.isGameOver());
        assertFalse(game.isWin());
    }

    @Test
    void testMakeGuess() throws GameException {
        // Проверяем угадывание слов
        List<String> words = Arrays.asList("книга");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Угадываем слово
        String result = game.makeGuess("книга");

        assertNotNull(result);
        assertEquals("+++++", result); // Полное совпадение
        assertEquals(1, game.getStep());
        assertTrue(game.isWin());
        assertTrue(game.isGameOver());
    }

    @Test
    void testInvalidGuessThrowsException() {
        // Проверяем обработку неверных слов
        List<String> words = Arrays.asList("книга", "ручка");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Слово не из словаря должно вызывать исключение
        assertThrows(GameException.class, () -> game.makeGuess("абвгд"));

        // Слишком короткое слово
        assertThrows(GameException.class, () -> game.makeGuess("книг"));

        // Слишком длинное слово
        assertThrows(GameException.class, () -> game.makeGuess("книгак"));
    }

    @Test
    void testWinCondition() throws GameException {
        // Проверяем выигрыш
        List<String> words = Arrays.asList("книга", "ручка", "чашка");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        // Создаем игру с фиксированным ответом
        WordleGame game = new WordleGame(dict, testLog) {
            private final String fixedAnswer = "книга";

            @Override
            public String getAnswer() {
                return fixedAnswer;
            }
        };

        // Угадываем правильное слово
        String result = game.makeGuess("книга");

        assertEquals("+++++", result);
        assertTrue(game.isWin());
        assertTrue(game.isGameOver());
        assertEquals(1, game.getStep());
    }

    @Test
    void testGetHint() throws GameException {
        // Проверяем получение подсказки
        List<String> words = Arrays.asList("книга", "ручка", "чашка", "ложка", "вилка");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Запрашиваем подсказку
        String hint = game.getHint();

        assertNotNull(hint);
        assertTrue(dict.contains(hint));
        assertEquals(5, hint.length()); // Слово должно быть из 5 букв
    }

    @Test
    void testMaxAttempts() throws GameException {
        // Проверяем максимальное количество попыток
        List<String> words = Arrays.asList("книга", "ручка", "чашка", "ложка", "вилка");
        WordleDictionary dict = new WordleDictionary(words);

        StringWriter stringWriter = new StringWriter();
        PrintWriter testLog = new PrintWriter(stringWriter);

        WordleGame game = new WordleGame(dict, testLog);

        // Делаем 6 попыток
        for (int i = 0; i < 6; i++) {
            game.makeGuess("ручка");
        }

        // Игра должна закончиться после 6 попыток
        assertTrue(game.isGameOver());
        assertEquals(6, game.getStep());
    }

    @Test
    void testGameExceptionMessage() {
        // Проверяем игровое исключение
        GameException exception = new GameException("Тестовая ошибка");
        assertEquals("Тестовая ошибка", exception.getMessage());
    }

    @Test
    void testEmptyDictionary() {
        // Проверяем работу с пустым словарем
        List<String> emptyList = Arrays.asList();
        WordleDictionary emptyDict = new WordleDictionary(emptyList);

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

        // Проверяем корректное сравнение одинаковой длины
        assertDoesNotThrow(() -> {
            WordleDictionary.compareWords("книга", "ручка");
        });
    }

    @Test
    void testFilterWords() {
        // Проверяем фильтрацию слов
        List<String> words = Arrays.asList("книга", "ручка", "чашка", "ложка", "вилка");
        WordleDictionary dict = new WordleDictionary(words);

        // Фильтруем слова, начинающиеся на "к"
        List<String> filtered = dict.filterWords("к____", "", "", "");

        assertTrue(filtered.contains("книга"));
        assertEquals(1, filtered.size());

        // Фильтруем слова, содержащие букву "а"
        filtered = dict.filterWords("", "а", "", "");
        assertTrue(filtered.contains("книга"));
        assertTrue(filtered.contains("чашка"));
        assertTrue(filtered.contains("ложка"));
        assertTrue(filtered.contains("вилка"));
    }
}