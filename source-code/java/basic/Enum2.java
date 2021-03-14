import java.util.Scanner;

public class Enum2{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextInt()){
            int n = sc.nextInt();
            System.out.printf("n=%d, count=%d\n", n, count(n));
        }
    }
    public static int count(int n){
        int count = 0;
        for(int i=2; i*i <= n; i++){
            if(n % i == 0){
                count += 2;
                if(i*i == n){
                    count -= 1;
                }
            }
        }
        return count;
    }
}