import java.util.Scanner;

public class SortExam{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextInt()){
            int n = sc.nextInt();
            if(n <= 0){
                break;
            }
            int[] a = new int[n];
            for(int i=0; i<n; i++){
                a[i] = sc.nextInt();
            }
            System.out.println("Results of bubble sort:");
            bubbleSort(a, 0, a.length);
            printAry(a);

            System.out.println("Results of select sort:");
            selectSort(a, 0, a.length);
            printAry(a);

            System.out.println("Results of quick sort:");
            quickSort(a, 0, a.length);
            printAry(a);
        }

    }
    
    static void printAry(int[] a){
        int n = a.length;
        for(int i=0; i<n; i++){
            if(i == n-1) System.out.printf("%d\n",a[i]);
            else System.out.printf("%d ", a[i]);
        }
    }

    static void bubbleSort(int[] a, int start, int end){
        for(int i=start; i<end; i++){
            for(int j=i; j<end; j++){
                if(a[i] > a[j]){
                    a[i] = a[i] ^ a[j];
                    a[j] = a[i] ^ a[j];
                    a[i] = a[i] ^ a[j];
                }
            }
        }
    }

    static void selectSort(int[] a, int start, int end){
        for(int i=start; i<end; i++){
            int minIndex = i;
            for(int j=i+1; j<end; j++){
                if(a[minIndex] > a[j]){
                    minIndex = j;
                }
            }
            int temp = a[i];
            a[i] = a[minIndex];
            a[minIndex] = temp;
        }
    }

    static void quickSort(int[] a, int start, int end){
        if(start >= end){
            return;
        }
        int left = start;
        int right = end-1;
        int temp = a[left];
        do{
            while(right > left && a[right] > temp){
                right--;
            }
            if(left < right){
                a[left] = a[right];
                left++;
            }
            while(left < right && a[left] < temp){
                left++;
            }
            if(left < right){
                a[right] = a[left];
                right--;
            }
        }while(left != right);
        a[left] = temp;
        quickSort(a, start, left-1);
        quickSort(a, left+1, right);
    }
}