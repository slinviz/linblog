import java.util.Scanner;

public class KKgame{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(true){
            int n = sc.nextInt();
            int m = sc.nextInt();
            if(n==0 && m ==0){
                break;
            }
            if(n%2==1 && m%2 == 1){
                System.out.println("What a pity!");
            }else{
                System.out.println("Wonderful!");
            }
        }
        sc.close();
    }
}