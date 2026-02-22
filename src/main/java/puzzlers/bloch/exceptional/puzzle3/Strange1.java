package puzzlers.bloch.exceptional.puzzle3;

public class Strange1 {
    public static void main(String[] args) {
        try{
           Missing m = new Missing(); // Declaration inside try
        } catch (java.lang.NoClassDefFoundError ex){
            System.out.println("Got it!");
        }
    }
}