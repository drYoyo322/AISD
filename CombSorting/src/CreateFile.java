import java.io.*;
import java.util.Random;


public class CreateFile {

    private int[] list = new int[60];
    private File file = new File("Data.txt");

    public File getFile() {
        return file;
    }

    public void fillFile(){
        File file = new File("Data.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fw = new FileWriter(file, false)) {
            fw.write("");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for(int i = 0; i < 60; i++){
            Random rand = new Random();
            int size = rand.nextInt(100, 10000);
            list[i] = size;
            try (FileOutputStream outputStream = new FileOutputStream(file, true)) {
                for (int j = 0; j < size; j++) {
                    String s = Integer.toString(rand.nextInt(0, 100000)) + " ";
                    outputStream.write(s.getBytes());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int[] getList() {
        return list;
    }
}