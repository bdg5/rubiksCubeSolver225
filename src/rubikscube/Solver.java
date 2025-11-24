package rubikscube;

import java.lang.invoke.MethodHandles;
import java.utils.PriorityQueue
public class Solver {

    private RubiksCube start;

	public static void main(String[] args) {
//		System.out.println("number of arguments: " + args.length);
//		for (int i = 0; i < args.length; i++) {
//			System.out.println(args[i]);
//		}

		if (args.length < 2) {
			System.out.println("File names are not specified");
			System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
			return;
		}

		// TODO
		File input = new File(args[0]);

        RubiksCube start = new RubiksCube(input); // throws IncorrectFormatException if input file is invalid

        PriortityQueue<RubiksCube> openQueue = new PriorityqQueue();
        // A* Search:
        openQueue.add(start);
        while (openQueue.isEmpty()) {
            RubiksCube v = openQueue.getMin();
            // get array of vertex's neighbors method
            // for (each neighbor u) {
            //      if (u == target/solvedState) {
            //          set g(u) = g(v) + 1;
            //          set u.parent = v;
            //          return u;
            //      } else {
            //          if (if u is in openQueue && f(u) is < f(u) stored in openQueue {
            //              f(u) of U in openQueue = new f(u)
            //          } else if (u is in ClosedSet && {
            //
            // }

        }

		// solve...
		//File output = new File(args[1]);

	}
}
