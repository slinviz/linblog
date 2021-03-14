import java.util.Scanner;

public class Pack{
    public static final int Inf = Integer.MIN_VALUE;

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
    }

    public static void initAry(int[] dp, int value){
        for(int i=0; i<dp.length; i++){
            dp[i] = value;
        }
    }

    public static void dim2ZeroOnePack(int[][] dp, int V, int U, int c1, int c2, int w){
        for(int v=V; v>=c1; v--){
            for(int u=U; u>=c2; u--){
                dp[v][u] = Math.max(dp[v][u], dp[v-c1][u-c2] + w);
            }
        }
    }

    public static void dim2CompletePack(int[][] dp, int V, int U, int c1, int c2, int w){
        for(int v=c1; v <= V; v++){
            for(int u=c2; u<=U; u++){
                dp[v][u] = Math.max(dp[v][u], dp[v-c1][u-c2] + w);
            }
        }
    }

    public static void dim2MultiplePack(int[][] dp, int V, int U, int c1, int c2, int w, int m){
        if(c1*m >= V && c2*m >= U){
            dim2CompletePack(dp, V, U, c1, c2, 2);
        }
        int k=1;
        while(k < m){
            dim2ZeroOnePack(dp, V, U, k*c1, k*c2, k*w);
            m = m - k;
            k = k << 1;
        }
        dim2ZeroOnePack(dp, V, U, m*c1, m*c2, m*w);
    }

    public static void zeroOnePack(int[] dp, int V, int c, int w){
        for(int v=V; v>=c; v--){
            // dp[i, v] = max(dp[i-1, v], dp[i-1, v-c] + w)
            dp[v] = Math.max(dp[v], dp[v-c]+w);
        }
    }

    public static void completePack(int[] dp, int V, int c, int w){
        for(int v=c; v<=V; v++){
            // dp[i, v] = max(dp[i-1, v], dp[i, v-c] + w)
            dp[v] = Math.max(dp[v], dp[v-c]+w);
        }
    }
    public static void multiplePack(int[] dp, int V, int c, int w, int m){
        if(c*m >= V){
            completePack(dp, V, c, w);
        }
        int k = 1;
        while(k < m){
            zeroOnePack(dp, V, k*c, k*w);
            m = m - k;
            k = k << 1;
        }
        zeroOnePack(dp, V, m*c, m*w);
    }
}