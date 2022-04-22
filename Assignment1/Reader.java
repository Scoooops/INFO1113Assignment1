import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Reader{

    public static void main(String[] args){
        try{
            FileInputStream f = new FileInputStream("traders");
            DataInputStream input = new DataInputStream(f);

            String n = input.readUTF();

            System.out.println(n);

            String m = input.readUTF();

            System.out.println(m);

            String o = input.readUTF();

            System.out.println(o);

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }


    }
}
