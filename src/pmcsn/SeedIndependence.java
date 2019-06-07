package pmcsn;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import static pmcsn.Configuration.*;

public class SeedIndependence {

    public static void main(String[] args) {

        Path path = Paths.get("../PMCSN_cloud/Matlab/seeds");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.print("C'è stato un errore durante la creazione della cartella\n");
            System.exit(1);
        }

        for (String s: seeds_collection){
            Rngs r = new Rngs();
            r.plantSeeds(Long.parseLong(s));
            PrintWriter writer=null;
            try {
                writer= new PrintWriter(new FileWriter("Matlab/seeds/" + "SeedStream" + s +  ".csv"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<String> streamValues = new ArrayList<>();
            for (int i=0; i<STOP_BATCH; i++) {
                streamValues.add(Double.toString(r.random()));
            }
            assert writer!=null;
            Util.print_on_file_column(writer, Util.convertArrayList(streamValues));
            writer.close();
        }

    }

}
