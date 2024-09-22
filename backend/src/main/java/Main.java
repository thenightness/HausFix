public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello World!");
        //Temporär damit Backend-Container nicht unnötig neugestartet wird
        while(true){
            Thread.sleep(1000);
        }
    }
}
