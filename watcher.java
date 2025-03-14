public class watcher implements Runnable {
    private volatile static boolean running = true;

    public static void stop() {
        try {
            running = false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

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