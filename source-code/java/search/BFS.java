import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

public class BFS{
    static int N = 500001;
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextInt()){
            int n = sc.nextInt();
            int k = sc.nextInt();
            int ans = bfs(n, k);
            System.out.printf("From n=%d to k=%d is %d\n", n, k, ans);
        }

    }

    static class Node{
        public int x = 0;
        public int dist = 0;
        public Node(){

        }
        public Node(int x, int dist){
            this.x = x;
            this.dist = dist;
        }
    }

    static int bfs(int n, int k){
        boolean[] vis = new boolean[N];
        for(int i=0; i<N; i++) vis[i] = false;
        
        Queue<Node> q = new LinkedList<>();
        q.add(new Node(n, 0));
        int result = 0;
        while(!q.isEmpty()){
            Node cur = q.poll();
            vis[cur.x] = true;
            if(cur.x == k){
                result = cur.dist;
                break;
            }

            if(cur.x+1 < N && !vis[cur.x+1]){
                q.add(new Node(cur.x+1, cur.dist+1));
            }
            if(cur.x-1 >= 0 && !vis[cur.x-1]){
                q.add(new Node(cur.x-1, cur.dist+1));
            }
            if(2*cur.x < N && 2*cur.x >=0 && !vis[2*cur.x]){
                q.add(new Node(2*cur.x, cur.dist+1));
            }
        }
        return result;
    }

    
}