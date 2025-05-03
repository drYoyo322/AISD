import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadNumbersToList {
    public  List<Integer> numbers;
    public ReadNumbersToList(){
        numbers = new ArrayList<>();
    }
    public void read(String path) {
        try (Scanner scanner = new Scanner(new File(path))) { //обращ по пути
            while (scanner.hasNextInt()) {
                numbers.add(scanner.nextInt());

            }
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
    public List<Integer> getList(){
        return numbers;
    }
    public int getSize(){
        return numbers.size();
    }
}