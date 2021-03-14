import java.util.Scanner;

public class Nim{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        while(n > 0){
            int sum = 0;
            for(int i=0; i< n;i++){
                sum = sum ^ sc.nextInt();
            }
            if(sum == 0){
                System.out.println("Grass Win!");
            }else{
                System.out.println("Rabbit Win!");
            }
            n = sc.nextInt();
        }
        sc.close();
    }
}