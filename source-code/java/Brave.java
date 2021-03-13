import java.util.Scanner;

public class Brave{
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        int C = scan.nextInt();
        for(int i=0; i<C; i++){
            int n = scan.nextInt();
            int m = scan.nextInt();
            if(n % (m+1) == 0){
                System.out.println("second");
            }else{
                System.out.println("first");
            }
        }
        scan.close();
    }
}