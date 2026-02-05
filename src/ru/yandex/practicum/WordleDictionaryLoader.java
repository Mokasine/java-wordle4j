package ru.yandex.practicum;

import java.io.*;
import java.util.*;

public class WordleDictionaryLoader {
    private PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary loadFromFile(String filename) throws IOException {
        List<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalizeWord(line);

                if (WordleDictionary.isValidWord(normalized)) {
                    words.add(normalized);
                }
            }

            log.println("Загружено " + words.size() + " слов из словаря");

        } catch (FileNotFoundException e) {
            log.println("Файл словаря не найден: " + filename);
            throw e;
        } catch (IOException e) {
            log.println("Ошибка чтения словаря: " + e.getMessage());
            throw e;
        }

        return new WordleDictionary(words);
    }
}