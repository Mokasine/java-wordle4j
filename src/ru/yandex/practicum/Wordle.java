package ru.yandex.practicum;

import java.io.*;
import java.util.Scanner;

public class Wordle {
    private static final String LOG_FILE = "wordle.log";
    private static final String DICTIONARY_FILE = "words_ru.txt"; // Изменено на words_ru.txt

    public static void main(String[] args) {
        PrintWriter log = null;

        try {
            // Создаем лог-файл
            log = new PrintWriter(new FileWriter(LOG_FILE, true));
            log.println("=== Игра Wordle начата ===");

            // Создаем загрузчик словарей и загружаем словарь
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
            WordleDictionary dictionary = loader.loadFromFile(DICTIONARY_FILE);

            if (dictionary.isEmpty()) {
                log.println("Ошибка: словарь пуст!");
                System.out.println("Ошибка: словарь пуст! Проверьте файл " + DICTIONARY_FILE);
                return;
            }

            // Создаем игру
            WordleGame game = new WordleGame(dictionary, log);

            // Игровой цикл
            Scanner scanner = new Scanner(System.in, "UTF-8");
            boolean gameRunning = true;

            System.out.println("Добро пожаловать в Wordle!");
            System.out.println("Угадайте слово из 5 букв. У вас 6 попыток.");
            System.out.println("Символы: '-' - буквы нет, '+' - буква на месте, '^' - буква есть, но не на месте");
            System.out.println("Для подсказки введите пустую строку (просто нажмите Enter)");

            while (gameRunning && !game.isGameOver()) {
                System.out.println("\nПопытка " + (game.getStep() + 1) + "/6");
                System.out.print("Введите слово: ");

                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    // Запрос подсказки
                    try {
                        String hint = game.getHint();
                        System.out.println("Подсказка: попробуйте слово '" + hint + "'");
                    } catch (GameException e) {
                        System.out.println("Не удалось получить подсказку: " + e.getMessage());
                    }
                    continue;
                }

                try {
                    String result = game.makeGuess(input);
                    System.out.println("Результат: " + result);

                    if (game.isWin()) {
                        System.out.println("\nПоздравляем! Вы угадали слово!");
                        gameRunning = false;
                    }
                } catch (GameException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                    System.out.println("Попробуйте еще раз.");
                }
            }

            if (!game.isWin() && game.isGameOver()) {
                System.out.println("\nИгра окончена! Вы проиграли.");
                System.out.println("Загаданное слово было: " + game.getAnswer());
            }

            scanner.close();

        } catch (IOException e) {
            System.out.println("Фатальная ошибка: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
            if (log != null) {
                log.println("Неожиданная ошибка: " + e.getMessage());
                e.printStackTrace(new PrintWriter(log));
            }
        } finally {
            if (log != null) {
                log.println("=== Игра Wordle завершена ===");
                log.close();
            }
        }
    }
}