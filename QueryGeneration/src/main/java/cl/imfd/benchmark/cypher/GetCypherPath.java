package cl.imfd.benchmark.cypher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.query.QueryException;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.algebra.AlgebraGenerator;
import org.apache.jena.sparql.algebra.Op;

public class GetCypherPath {

	static String INPUT = "../Queries/paths-can.txt";

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(INPUT));
		FileWriter outputFile = new FileWriter("../Queries/Cypher/cypher_paths.txt");

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			String[] cols = line.trim().split(",");

			String query = "SELECT * WHERE { " + cols[1] + " }";

			Op op = null;
			GetCypherPathVisitor visitor = new GetCypherPathVisitor();

			try {
				op = (new AlgebraGenerator()).compile(QueryFactory.create(query));
				op.visit(visitor);
			} catch (QueryParseException e) {
				System.out.println(line);
				System.out.println(e);
				continue;
			} catch (QueryException e) {
				System.out.println(line);
				System.out.println(e);
				continue;
			}

			if (!visitor.hasUnsupportedOp) {
				outputFile.write(cols[0]);
				outputFile.write(",MATCH ");
				outputFile.write(visitor.getCypherPath());
				outputFile.write(" RETURN *\n");
			}
		}
		outputFile.close();
		br.close();
	}
}
