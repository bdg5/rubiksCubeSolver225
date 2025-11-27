package rubikscube;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.PriorityQueue;


public class Solver {
    private static int FRONT = 0;
    private static int BACK = 1;
    private static int RIGHT = 2;
    private static int LEFT = 3;
    private static int UP = 4;
    private static int DOWN = 5;

	private static class SearchNode implements Comparable<SearchNode> {
		
		int Cost; // F = G + H
		int DistFromStart; // G
		int Heuristic; // H
		RubiksCube CurrState; // This is state of the cube
		String path; // Stores the instructions that it took to get to this specific state

		public SearchNode( int DistFromStart, int Heuristic, RubiksCube Curr_State, String path){
			this.DistFromStart = DistFromStart;
			this.Heuristic = Heuristic;
			this.CurrState = Curr_State;
			this.path = path;
			Cost = DistFromStart + Heuristic;
		}

		public int compareTo( SearchNode other ){
			if( this.Cost > other.Cost ) return 1; // The 1 represents true that our cost is greater than the other cost
			else if ( this.Cost < other.Cost ) return -1; // The -1 represents that the other cost function is greater than this cost
			else return 0; // The zero represents equals 
		}
	}

    public static int numOfIncorrectEdges(RubiksCube Cube) {
        int sum = 0;
        // Count the number of edges (12 in total)
        if (Cube.state[UP][1][2] != 'O' || Cube.state[FRONT][1][0] != 'W') sum++;
        if (Cube.state[UP][0][1] != 'O' || Cube.state[LEFT][1][0] != 'G') sum++;
        if (Cube.state[UP][2][1] != 'O' || Cube.state[RIGHT][1][0] != 'B') sum++;
        if (Cube.state[UP][1][0] != 'O' || Cube.state[BACK][1][0] != 'Y') sum++;

        if (Cube.state[FRONT][0][1] != 'W' || Cube.state[LEFT][2][1] != 'G') sum++;
        if (Cube.state[FRONT][2][1] != 'W' || Cube.state[RIGHT][0][1] != 'B') sum++;
        if (Cube.state[BACK][0][1] != 'Y' || Cube.state[RIGHT][2][1] != 'G') sum++;
        if (Cube.state[BACK][2][1] != 'Y' || Cube.state[LEFT][0][1] != 'B') sum++;

        if (Cube.state[DOWN][1][2] != 'R' || Cube.state[BACK][1][2] != 'Y') sum++;
        if (Cube.state[DOWN][0][1] != 'R' || Cube.state[LEFT][1][2] != 'G') sum++;
        if (Cube.state[DOWN][2][1] != 'R' || Cube.state[RIGHT][1][2] != 'B') sum++;
        if (Cube.state[DOWN][1][0] != 'R' || Cube.state[FRONT][1][2] != 'W') sum++;
        return sum;
    }

    public static int numOfIncorrectCorners(RubiksCube Cube) {
        int sum = 0;
        // Count the number of incorrect corners (8 in total)
        if (Cube.state[UP][0][0] != 'O' || Cube.state[LEFT][0][0] != 'G' || Cube.state[BACK][2][0] != 'Y') sum++;
        if (Cube.state[UP][2][0] != 'O' || Cube.state[RIGHT][2][0] != 'B' || Cube.state[BACK][0][0] != 'Y') sum++;
        if (Cube.state[UP][0][2] != 'O' || Cube.state[LEFT][2][0] != 'G' || Cube.state[FRONT][0][0] != 'W') sum++;
        if (Cube.state[UP][2][2] != 'O' || Cube.state[RIGHT][0][0] != 'B' || Cube.state[FRONT][2][0] != 'W') sum++;

        if (Cube.state[DOWN][0][0] != 'R' || Cube.state[LEFT][2][2] != 'G' || Cube.state[FRONT][0][2] != 'W') sum++;
        if (Cube.state[DOWN][2][0] != 'R' || Cube.state[RIGHT][0][2] != 'B' || Cube.state[FRONT][2][2] != 'W') sum++;
        if (Cube.state[DOWN][0][2] != 'R' || Cube.state[LEFT][0][2] != 'G' || Cube.state[BACK][2][2] != 'Y') sum++;
        if (Cube.state[DOWN][2][2] != 'R' || Cube.state[RIGHT][2][2] != 'B' || Cube.state[BACK][0][2] != 'Y') sum++;
        return sum;
    }
	public static int calculateHeuristic( RubiksCube Cube ){
        // Heuristic v2.0: counts the number of edge and corner pieces in the wrong spot
        int incorrectNum = 0; // Number of incorrect pieces

        incorrectNum += numOfIncorrectEdges(Cube);
        incorrectNum += numOfIncorrectCorners(Cube);

        // *DEPRECATED* Heuristic v1.0
//        char correctColour = 'W'; // Just set to W to initialize
//        for (int i = 0; i < 6; i++) {
//            if (i == 0) correctColour = 'W';
//            if (i == 1) correctColour = 'Y';
//            if (i == 2) correctColour = 'B';
//            if (i == 3) correctColour = 'G';
//            if (i == 4) correctColour = 'O';
//            if (i == 5) correctColour = 'R';
//
//            if (Cube.state[i][0][0] != correctColour) incorrectNum++;
//            if (Cube.state[i][0][2] != correctColour) incorrectNum++;
//            if (Cube.state[i][2][0] != correctColour) incorrectNum++;
//            if (Cube.state[i][2][2] != correctColour) incorrectNum++;
//        }
        // 9 comes from the maximum number of corners fixed during turn (3) * num of stickers in each corner
        // Need highest number so we dont overestimate the heuristc
		// (UPDATE) If you you turn a face of solved cube it displaces 4 corners. 
		// Each corner has 3 stickers so 4*3 = 12 incorrect ceil(12/9) = 2 should be 1 because we know its only 1 move away

        return (int)Math.ceil(incorrectNum / 8.0); // 2.0: Divide by 8 because we would fix at most 8 pieces at a time
	}
	public static void main(String[] args) throws IOException, IncorrectFormatException {
//		System.out.println("number of arguments: " + args.length);
//		for (int i = 0; i < args.length; i++) {
//			System.out.println(args[i]);
//		}
		HashSet<RubiksCube> visited = new HashSet<>();
		RubiksCube SolvedState = new RubiksCube();
		int solvedHash = SolvedState.hashCode();

		if (args.length < 2) {
			System.out.println("File names are not specified");
			System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
			return;
		}
		
		
		// TODO
		//File input = new File(args[0]);
		String Input_Filename = args[0];
		RubiksCube StartCube = new RubiksCube( Input_Filename );
		String StartPath = "";
		int StartHeuristic = calculateHeuristic( StartCube );
		
		PriorityQueue<SearchNode> Queue = new PriorityQueue<>();

		String SolvedPath = "";
		// Init the start node
		SearchNode StartNode = new SearchNode( 0, StartHeuristic, StartCube, StartPath );

		Queue.add(StartNode);

		char[] moves = {'F', 'B', 'L', 'R', 'U', 'D'}; // For the inner Loop

		while( !Queue.isEmpty() ){
			SearchNode Current = Queue.poll();
			visited.add( Current.CurrState );
			for( int i = 0; i < 6; i++ ){
				RubiksCube CurrentNeighbour = Current.CurrState.getNeighbor( moves[i] );
				if( !visited.contains( CurrentNeighbour ) ){
					// If this node has not been visited yet then add it to the priority queue
					SearchNode NeighbourNode = new SearchNode( Current.DistFromStart + 1, calculateHeuristic(CurrentNeighbour), CurrentNeighbour, Current.path + moves[i] );
					Queue.add( NeighbourNode );
					visited.add( CurrentNeighbour );
					if( CurrentNeighbour.hashCode() == solvedHash ){
						SolvedPath = NeighbourNode.path;
						// args[1] = SolvedPath; args[1] only houses the name of the file that you need to write to 
						// need to create a function that writes the string to the file args[1]
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {
						writer.write(SolvedPath);
						System.out.println("Text written to the file successfully.");
                        return;
					} catch (IOException e) {
						System.out.println("An error occurred while writing to the file: " + e.getMessage());
						e.printStackTrace();
					}
    			}
			}
		}
	}
}
		// solve...
		//File output = new File(args[1]);
}

