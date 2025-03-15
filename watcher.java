public class watcher implements Runnable {
    private volatile static boolean running = true;
    // Method to stop the thread
    public static void stop() {
        try {
            running = false; // flag to stop the thread
        } catch (Exception e) {
            //DEBUGGING
        }
    }
    // Method to start the thread
    @Override
    public void run() {
        while (running) {
            UserInterface.readName(); // Call the function
            try {
                Thread.sleep(1000); // Sleep for 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}