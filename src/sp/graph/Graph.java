package sp.graph;


public interface Graph {
	void addEdge(int src, int dest, int weight);
	Integer[] shortestPath(int src, boolean multithreaded);
}
