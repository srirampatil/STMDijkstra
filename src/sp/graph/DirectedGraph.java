package sp.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import jvstm.CommitException;
import jvstm.Transaction;
import jvstm.VBox;

public class DirectedGraph implements Graph {

	private VBox<Boolean> done = new VBox<Boolean>();

	private class Node {
		int data;
		Edge first;
		Edge last;

		public Node(int val) {
			data = val;
			first = null;
			last = null;
		}

		public void addEdge(int dest, int weight) {
			Edge e = new Edge(dest, weight);
			if (first == null)
				first = last = e;
			else {
				last.next = e;
				last = e;
			}
		}
	}

	private class Edge {
		int dest, weight;
		Edge next;

		public Edge(int d, int w) {
			dest = d;
			weight = w;
			next = null;
		}
	}

	List<Node> nodeList;

	public DirectedGraph(int numberOfNodes) {
		nodeList = new ArrayList<Node>(numberOfNodes);
		for (int i = 1; i <= numberOfNodes; i++)
			nodeList.add(new Node(i));
	}

	@Override
	public void addEdge(int src, int dest, int weight) {
		Node n = nodeList.get(src - 1);
		n.addEdge(dest, weight);
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < nodeList.size(); i++) {
			strBuilder.append(nodeList.get(i).data + " -> ");
			Edge e = nodeList.get(i).first;
			while (e != null) {
				strBuilder.append(e.dest + ",");
				e = e.next;
			}

			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}

	private class Path {
		Node n;
		int distance;

		public Path(Node n, int d) {
			this.n = n;
			distance = d;
		}
	}

	private PriorityQueue<Path> pq;
	private Integer[] distanceList;

	public Integer[] shortestPath(int src, boolean multithreaded) {
		distanceList = new Integer[nodeList.size()];
		for (int i = 0; i < nodeList.size(); i++)
			distanceList[i] = Integer.MAX_VALUE;

		distanceList[src - 1] = 0;

		pq = new PriorityQueue<>(nodeList.size(), new Comparator<Path>() {

			@Override
			public int compare(Path p1, Path p2) {
				return p1.distance - p2.distance;
			}
		});

		pq.add(new Path(nodeList.get(src - 1), 0));

		Thread t = null;
		if (multithreaded) {
			t = new Thread(new Runnable() {

				@Override
				public void run() {
					helperShortestPath();
				}
			});
		}

		while (!pq.isEmpty()) {
			Path p = pq.poll();
			done.put(false);

			if (multithreaded) {
				if (!t.isAlive()) {
					try {
						t.start();
					} catch(IllegalThreadStateException e) {
						// Unable to start the thread
					}
				}
			}

			Edge adEdge = nodeList.get(p.n.data - 1).first;
			while (adEdge != null) {
				int newWeight = distanceList[p.n.data - 1] + adEdge.weight;

				Transaction.begin();
				if (distanceList[adEdge.dest - 1] > newWeight) {
					Path newPath = new Path(nodeList.get(adEdge.dest - 1),
							newWeight);
					pq.add(newPath);
					distanceList[adEdge.dest - 1] = newWeight;
				}
				Transaction.commit();

				adEdge = adEdge.next;
			}

			Transaction.begin();
			done.put(true);
			Transaction.commit();
		}

		return distanceList;
	}

	private void helperShortestPath() {
		while (!pq.isEmpty()) {
			while (done.get())
				;

			Path p = pq.peek();
			boolean stop = false;

			Edge adEdge = nodeList.get(p.n.data - 1).first;
			while (adEdge != null && !stop) {

				try {
					Transaction.begin();
					if (!done.get()) {
						int newWeight = distanceList[p.n.data - 1]
								+ adEdge.weight;

						if (distanceList[adEdge.dest - 1] > newWeight) {
							Path newPath = new Path(
									nodeList.get(adEdge.dest - 1), newWeight);
							pq.add(newPath);
							distanceList[adEdge.dest - 1] = newWeight;
						}
					} else
						stop = true;

					adEdge = adEdge.next;

					Transaction.commit();
				} catch (CommitException e) {
					Transaction.abort();
					System.out.println("Secondary thread aborted!");
					return;
				}
			}
		}
	}
}
