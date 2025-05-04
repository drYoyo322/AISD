import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FileCreatorRandomizer {
    private static final int FILE_COUNT = 60;
    private static final int MAX_NUMBERS_PER_FILE = 10_000;
    private static final int MIN_NUMBER = 0;
    private static final int MAX_NUMBER = 100_000;
    private static final String OUTPUT_DIRECTORY = "random_numbers";

    public static void main(String[] args) {
        createOutputDirectory();
        generateRandomNumberFiles();
    }

    private static void createOutputDirectory() {
        File dir = new File(OUTPUT_DIRECTORY);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("Директория создана: " + dir.getAbsolutePath());
            } else {
                System.err.println("Не удалось создать директорию!");
                System.exit(1);
            }
        }
    }

    private static void generateRandomNumberFiles() {
        Random random = new Random();
        int[] numbersCountPerFile = new int[FILE_COUNT];

        for (int i = 0; i < FILE_COUNT; i++) {
            String fileName = OUTPUT_DIRECTORY + "/numbers_" + (i + 1) + ".txt";
            int numbersCount = random.nextInt(MAX_NUMBERS_PER_FILE) + 1; // От 1 до 10 000 чисел
            numbersCountPerFile[i] = numbersCount;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (int j = 0; j < numbersCount; j++) {
                    int randomNumber = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
                    writer.write(String.valueOf(randomNumber));

                    // Добавляем пробел, если это не последнее число в файле
                    if (j < numbersCount - 1) {
                        writer.write(" ");
                    }

                    // Добавляем перенос строки каждые 20 чисел для читаемости
                    if ((j + 1) % 20 == 0) {
                        writer.newLine();
                    }
                }
                System.out.printf("Создан файл %s с %d числами%n", fileName, numbersCount);
            } catch (IOException e) {
                System.err.println("Ошибка при создании файла " + fileName + ": " + e.getMessage());
            }
        }

        // Сохраняем информацию о количестве чисел в каждом файле
        saveMetadata(numbersCountPerFile);
    }

    private static void saveMetadata(int[] numbersCountPerFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIRECTORY + "/metadata.txt"))) {
            for (int i = 0; i < numbersCountPerFile.length; i++) {
                writer.write(String.format("Файл numbers_%d.txt содержит %d чисел%n",
                        i + 1, numbersCountPerFile[i]));
            }
            System.out.println("Метаданные сохранены в metadata.txt");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении метаданных: " + e.getMessage());
        }
    }
}