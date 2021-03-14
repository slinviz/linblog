import java.util.Scanner;


public class SpringDFS{
    static class Location{
        public int x = 0;
        public int y = 0;
        public Location(){}
        public Location(int x, int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString(){
            return "localtion< " + x + " , " + y + " >";
        }
    }

    static int[][] map;
    static boolean[][] vis;
    static int[][] dir = {{0,1}, {0, -1}, {1,0}, {-1, 0}};

    static int ans = 0;

    static void dfs(int x, int y, int n, int m, int height){
        vis[x][y] = true;
        ans += 1;
        for(int i=0; i<4; i++){
            int nx = x + dir[i][0];
            int ny = y + dir[i][1];
            if(nx>=0 && nx < n && ny >=0 && ny < m && !vis[nx][ny] && map[nx][ny] <= height){
                dfs(nx, ny, n, m, height);
            }
        }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextInt()){
            int n = sc.nextInt();
            int m = sc.nextInt();
            int q1 = sc.nextInt() - 1;
            int q2 = sc.nextInt() - 1;

            ans=0;
            map = new int[n][m];
            vis = new boolean[n][m];
            for(int i=0; i< n; i++){
                for(int j=0; j<m; j++){
                    int h = sc.nextInt();
                    map[i][j] = h;
                    vis[i][j] = false;
                }
            }
            dfs(q1, q2, n, m, map[q1][q1]);
            System.out.println(ans);
        }
    }
}