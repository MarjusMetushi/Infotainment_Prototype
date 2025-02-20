// Class that always runs in the background and is public static

import java.io.File;
import java.util.Stack;

public class audio {
    Stack<File> NextOnes = new Stack<>();
    Stack<File> PreviousOnes = new Stack<>();
    public static void play(){}
    public static void stop(){}
    public static void pause(){}
    public static void next(){}
}
