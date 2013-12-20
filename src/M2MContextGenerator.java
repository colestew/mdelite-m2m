import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;

import CoreMDELite.vm2t.DefaultContextGenerator;
import CoreMDELite.vm2t.Model;


abstract class M2MContextGenerator extends DefaultContextGenerator {
	Model m;
	
	private void checkSchema(String table, String[] values) {
		if (m.schemas.get(table).size() != values.length) {
			String err = "Wrong number of variables supplied to table "
					+ table + ": " + values.length;
			throw new IllegalArgumentException(err);
		}
	}
	
	/**
	 * Tables in the model have "S" appended to the end. This should
	 * only be for use in this class.
	 * @param table
	 * @return
	 */
	private static String toModelTable(String table) {
		return table + "S";
	}
	
	/**
	 * Returns the head of some list.
	 * @param l
	 * @return
	 */
	protected static String head(List<String> l) {
		return l.get(0);
	}
	
	/**
	 * Returns the tail of some list
	 * @param l
	 * @return
	 */
	protected static List<String> tail(List<String> l) {
		List<String> result = new ArrayList<String>(l);
		result.remove(0);
		return result;
	}
	
	/**
	 * Adds some fact with values to a table. The values
	 * will be bound to atoms in order of the schema for
	 * the table.
	 * @param table
	 * @param values
	 */
	protected void addFact(String table, String... values) {
		checkSchema(table, values);
		List<String> schema = m.schemas.get(table);
		Map<String, String> row = new HashMap<String, String>();
		for (int i = 0; i < values.length; ++i) {
			row.put(schema.get(i), values[i]);
		}
		
		m.tables.get(toModelTable(table)).add(row);
	}
	
	/**
	 * Returns all facts from table that bind to atomName
	 * @param atomName
	 * @param table
	 * @param values
	 * @return
	 */
	protected List<Map<String, String>> findAllFacts(String atomName, String table, String... values) {
		checkSchema(table, values);
		List<String> schema = m.schemas.get(table);
		List<Map<String, String>> facts = m.tables.get(toModelTable(table));
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		for (Map<String, String> fact: facts) {
			Atom atom = new Atom(atomName);
			for (int i = 0; i < values.length; ++i) {
				String key = schema.get(i);
				String value = fact.get(key);
				if (values[i] ==null) {
					if (atom.name.equals(key)) {
						atom.value = value;
					}
				} else if (value.equals(values[i]) && atom.name.equals(key)) {
					atom.value = value;
				} else if (!value.equals(values[i])) {
					atom.value = null;
					break;
				}
			}
			
			if (atom.value != null) {
				result.add(fact);
			}
		}
		
		return result;
	}
	
	/**
	 * Finds the first fact that matches the variables found in values.
	 * @param atomName
	 * @param table
	 * @param values
	 * @return
	 */
	protected Map<String, String> findFirstFact(String atomName, String table, String... values) {
		return findAllFacts(atomName, table, values).get(0);
	}
	
	/**
	 * Removes the first occurrence of some fact that matches the variables found
	 * in values.
	 * @param atomName
	 * @param table
	 * @param values
	 * @return
	 */
	protected String findFirst(String atomName, String table, String... values) {
		return findAll(atomName, table, values).get(0);
	}
	
	/**
	 * Removes a fact from some table.
	 * @param table
	 * @param fact
	 */
	protected void removeFact(String table, Map<String, String> fact) {
		m.tables.get(toModelTable(table)).remove(fact);
	}
	
	/**
	 * Fields should be sequential strings that fetch for data in
	 * the table. If a value is null it is somewhat equivalent to a '_'
	 * in Prolog. The end result is every possible bound value of
	 * the atom with name atomName. 
	 * @param table
	 * @param atom
	 * @param values
	 */
	protected List<String> findAll(String atomName, String table, String... values) {
		List<Map<String, String>> facts = findAllFacts(atomName, table, values);
		List<String> result = new ArrayList<String>(facts.size());
		for (Map<String, String> fact : facts) {
			result.add(fact.get(atomName));
		}
		return result;
	}
	
	/**
	 * Simple class for use in find methods above
	 * @author colestewart
	 */
	protected static class Atom {
		public final String name;
		public String value;
		
		public Atom(String name) {
			this.name = name;
			value = null;
		}
	}
	
	@Override
	public VelocityContext generateContext(Model m) {
		this.m = m;
		transformModel();
		return super.generateContext(m);
	}
	
	/**
	 * Override this method and modify model m before
	 * it is passed to velocity
	 */
	abstract public void transformModel();
}
