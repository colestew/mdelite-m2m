import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TT2BDD_ContextGenerator extends M2MContextGenerator {
	private static final String TABLE_EDGE = "edge";
	private static final String TABLE_NODE = "node";
	private static final String TABLE_ROW = "rows";
	private static final String TABLE_PORT = "ports";
	private static final String TABLE_CELLS = "cells";
	private static final String TABLE_INPUTS = "inputs";
	private static final String TABLE_OUTPUTS = "outputs";

	private int nodeId = 0;

	private List<String> remove(String s, List<String> l) {
		List<String> result = new ArrayList<String>(l);
		result.remove(s);
		return result;
	}
	
	private List<String> inputs(List<String> ports) {
		List<String> result = new ArrayList<String>(ports);
		result.retainAll(findAll("pid", TABLE_INPUTS, null, null));
		return result;
	}

	private List<String> outputs(List<String> ports) {
		List<String> result = new ArrayList<String>(ports);
		result.retainAll(findAll("pid", TABLE_OUTPUTS, null, null));
		return result;
	}

	private String findLeastUndefinedInput(List<String> ports, List<String> rows) {
		ports = inputs(ports);
		int minUndef = Integer.MAX_VALUE;
		String minPortId = null;
		for (String port : ports) {
			int undefCount = 0;
			for (String row: rows) {
				String val = findFirst("value", TABLE_CELLS, null, null, port, row);
				if (val.equals("null")) {
					++undefCount;
				}
			}
			if (undefCount < minUndef) {
				minPortId = port;
				minUndef = undefCount;
			}
		}
		return minPortId;
	}

	private String toIntBool(String truthVal) {
		return truthVal.equals("true") ? "1" : "0";
	}

	private List<String> findNextRows(String port, List<String> rows, String value) {
		List<String> result = findAll("rid", TABLE_CELLS, null, value, port, null);
		result.addAll(findAll("rid", TABLE_CELLS, null, "null", port, null));
		result.retainAll(rows);
		return result;
	}

	private void minimizeTree(int nodeId) {
		/*A->B->C
		 *    \->D
		 * :-
		 * A->D
		 */

		// get the child edge to remove
		List<Map<String, String>> childEdges = findAllFacts("pnid", TABLE_EDGE, "n"+nodeId, null, null);
		String nidToRemove = childEdges.get(0).get("cnid");
		String nidToModify = childEdges.get(1).get("cnid");
		Map<String, String> currentNode = findFirstFact("nid", TABLE_NODE, "n"+nodeId, null);
		Map<String, String> childToRemove = findFirstFact("nid", TABLE_NODE, nidToRemove, null);
		Map<String, String> childToModify = findFirstFact("nid", TABLE_NODE, nidToModify, null);

		super.removeFact(TABLE_NODE, currentNode);
		super.removeFact(TABLE_EDGE, childEdges.get(0));
		super.removeFact(TABLE_EDGE, childEdges.get(1));
		super.removeFact(TABLE_NODE, childToRemove);

		// point the parent to the new node
		Map<String, String> parentEdge = findFirstFact("cnid", TABLE_EDGE, null, "n"+nodeId, null);
		parentEdge.put("cnid", childToModify.get("nid"));
	}

	private String createTreeHelper(int parentNodeId, String edgeValue, List<String> ports, List<String> rows) {
		if (ports.size() == 0 || rows.size() == 0) return null;

		String r = head(rows);
		int newNodeId = ++nodeId;			

		String p = null, portName = null, result = null;

		// If we still need to continue drawing inputs
		if (inputs(ports).size() > 0) {
			p = findLeastUndefinedInput(ports, rows);
			portName = findFirst("name", TABLE_PORT, p, null, null);
		}

		// If we're finally at a result
		else if (outputs(ports).size() > 0) {
			p = head(outputs(ports));
			portName = findFirst("value", TABLE_CELLS, null, null, p, r);
			portName = toIntBool(portName);
			result = portName;
		}

		addFact(TABLE_NODE, "n"+newNodeId, portName.replace('\'', '\"'));
		if (parentNodeId != 0) {  // no need to add an edge with no parent
			addFact(TABLE_EDGE, "n"+parentNodeId, "n"+newNodeId, edgeValue);
		}
		if (rows.size() >= 1) {
			List<String> falseRows = findNextRows(p, rows, "false");
			List<String> trueRows = findNextRows(p, rows, "true");
			String result1 = createTreeHelper(newNodeId, "0", remove(p, ports), falseRows);
			String result2 = createTreeHelper(newNodeId, "1", remove(p, ports), trueRows);
			if (result1 != null && result2 != null && result1.equals(result2)) {
				minimizeTree(newNodeId);
				return result1;
			}
		} 

		return result;
	}


	private void createTree() {
		try {
			List<String> ports = findAll("pid", TABLE_PORT, null, null, null);
			List<String> rows = findAll("rid", TABLE_ROW, null, null);

			createTreeHelper(nodeId, null, ports, rows);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void transformModel() {
		createTree();
	}
}
