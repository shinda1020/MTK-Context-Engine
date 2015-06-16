package mtk.test;

import mtk.app.*;

import java.util.LinkedList;

/**
 * @author shinda
 * 
 */
public class CETest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		 StoryBall sb = new StoryBall();
		 sb.start();

	}

//	static int friendCircles(String[] friends) {
//
//		int size = friends.length;
//		int[][] graph = new int[size][size];
//
//		int numOfCircles = 0;
//
//		boolean[] visited = new boolean[size];
//
//		/*
//		 * Initialized
//		 */
//
//		for (int i = 0; i < size; ++i) {
//			String temp = friends[i];
//			for (int j = 0; j < size; ++j) {
//				char c = temp.charAt(j);
//				graph[i][j] = (c == 'Y') ? 1 : 0;
//			}
//		}
//
//		for (int i = 0; i < size; ++i) {
//			visited[i] = false;
//		}
//
//		LinkedList<Integer> tempList = new LinkedList<Integer>();
//
//		while (!isAllVisited(visited)) {
//
//			// Get the first unvisited node
//			int curNode = firstUnvisitedNode(visited);
//
//			int[] curRelation = graph[curNode];
//			visited[curNode] = true;
//
//			for (int i = 0; i < curRelation.length; ++i) {
//				if ((curRelation[i] == 1) && !visited[i]) {
//					visited[i] = true;
//					tempList.addLast(i);
//				}
//			}
//
//			// Iterate all nodes that are connected to the nodes in the list
//			while (tempList.size() > 0) {
//				int tempNode = tempList.removeFirst();
//				int[] tempRelation = graph[tempNode];
//
//				for (int i = 0; i < tempRelation.length; ++i) {
//					if ((tempRelation[i] == 1) && !visited[i]) {
//						visited[i] = true;
//						tempList.addLast(i);
//					}
//				}
//			}
//
//			numOfCircles++;
//		}
//
//		return numOfCircles;
//	}
//
//	static int firstUnvisitedNode(boolean[] visited) {
//		for (int i = 0; i < visited.length; ++i) {
//			if (!visited[i]) {
//				return i;
//			}
//		}
//
//		return -1;
//	}
//
//	static boolean isAllVisited(boolean[] visited) {
//		for (int i = 0; i < visited.length; ++i) {
//			if (!visited[i]) {
//				return false;
//			}
//		}
//
//		return true;
//	}

}
