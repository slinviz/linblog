import java.util.Scanner;

public class Test{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        sc.nextLine();
        for(int i=0; i<n; i++){
            System.out.println(sc.nextLine());
        }
        sc.close();
    }
}