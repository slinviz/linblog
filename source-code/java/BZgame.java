import java.util.Scanner;

public class BZgame{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNext()){
            int a = sc.nextInt();
            int b = sc.nextInt();
            int ak = Math.max(a, b);
            int bk = Math.min(a, b);
            int k = ak - bk;
            int temp = (int)(k * (1+Math.sqrt(5))/2);
            if(temp == bk){
                System.out.println(0);
            }else{
                System.out.println(1);
            }
        }
        sc.close();
    }
}