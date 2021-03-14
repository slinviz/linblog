import java.util.Scanner;

public class Enum1{

    public static boolean check(int x){
        for(int i=2; i*i<= x; i++){
            if(x % i == 0){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextInt()){
            int n = sc.nextInt();
            for(int i=2; i*n < 1000; i++){
                if(check(i)){
                    System.out.printf("%d/%d=%d\n", i*n, i, n);
                }
            }
        }
    }
}