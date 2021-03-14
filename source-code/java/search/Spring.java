import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;

public class Spring{
    static class Location{
        int x = 0;
        int y = 0;
        public Location(){}
        public Location(int x, int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString(){
            return "Location:<" + x + ", " + y +" >";
        }
    }
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextInt()){
            int n = sc.nextInt();
            int m = sc.nextInt();
            int q1 = sc.nextInt();
            int q2 = sc.nextInt();

            int[][] map = new int[n][m];
            boolean[][] vis = new boolean[n][m];
            for(int i=0; i<n; i++){
                for(int j=0; j<m; j++){
                    int h = sc.nextInt();
                    map[i][j] = h;
                    vis[i][j] = false;
                }
            }

            int ans = 0;
            Queue<Location> q = new LinkedList<>();
            q.add(new Location(q1-1, q2-1));
            int[][] dir = new int[4][2];
            dir[0][0] = 0;  dir[0][1] = 1;
            dir[1][0] = 0;  dir[1][1] = -1;
            dir[2][0] = 1;  dir[2][1] = 0;
            dir[3][0] = -1; dir[3][1] = 0;
            while(!q.isEmpty()){
                Location cur = q.poll();
                vis[cur.x][cur.y] = true;
                ans += 1;
                for(int i=0; i<4; i++){
                    int x = cur.x + dir[i][0];
                    int y = cur.y + dir[i][1];
                    if(x>=0 && x<n && y>=0 && y<m && !vis[x][y] && map[x][y] <= map[q1-1][q2-1]){
                        q.add(new Location(x,y));
                    }
                }
            }
            System.out.println(ans);

        }
    }
}