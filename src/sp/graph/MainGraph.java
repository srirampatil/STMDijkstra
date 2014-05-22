package sp.graph;

import java.util.Scanner;


public class MainGraph {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int vertices = sc.nextInt();
		int edges = sc.nextInt(); 
		
		Graph g = new DirectedGraph(vertices);
		
		for(int i = 0; i < edges; i++) {
			int v = sc.nextInt();
			int u = sc.nextInt();
			int w = sc.nextInt();
			g.addEdge(v, u, w);
		}
		
		sc.close();
		System.out.println();
		System.out.println(g.toString());

		long time = System.currentTimeMillis();
		
		Integer[] distanceList = g.shortestPath(1, false);
		System.out.println("Shortest distance from 1 to 5: " + distanceList[vertices - 1]);
		
		System.out.println("Time: " + (System.currentTimeMillis() - time) / 1000.0 + " secs");
	}
}
