import java.io.*;
public class TokenManager {
    
    private static final String mapsTokenFile = "mapsToken.txt";
    private static final String spotifyTokenFile = "spotifyToken.txt";
    private static final String youtubeTokenFile = "youtubeToken.txt";

    public static void save(String token, String fileName){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write(token);
        }catch(Exception e){
            e.printStackTrace();
        }
    } 
    public static String load(String fileName){
        File file = new File(fileName);
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            return reader.readLine();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
}