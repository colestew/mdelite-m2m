mdelite-m2m
===========

We were never able to complete the necessary Prolog transformation for the TT2BDD transformation, but, we ended up creating a small API to the VelocityContext in Java that populates our node and edge table before the model is finally handed off to vm2t. It takes advantage of the [-cg] flag to vm2t that can specify a context generator. This can be found in 

mde/tt2bdd/ModelToModelTransformer/src/M2MTContextGenerator.java

It is an abstract class that extends DefaultContextGenerator. It has a bunch of helper functions for subclasses to use. The following excerpt is enough to explain how it works I believe (sorry for the bright text):
    
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


There is also some nice helper functions that allow you to find Prolog "facts" from the model:

protected List<Map<String, String>> findAllFacts(String atomName, String table, String... values)
protected Map<String, String> findFirstFact(String atomName, String table, String... values)
protected List<String> findAll(String atomName, String table, String... values)
protected String findFirst(String atomName, String table, String... values)

Each of the find "matching" methods has the same arguments: (String atomName, String table, String... values)
They work by supplying some atom name, or column name from a table, a table, and a list of values to match. Null represents a "_" in Prolog.
For instance, say I have the following table defined:

table(ports,[portid,name,truthtableid]).

I could call findAll("name", "ports", null, null, "t1") and return a list of every name that is in truth table with id t1.
I could also call findAllFacts("name", "ports", null, null, "t1") and retrieve a List of every row from the table
 (which is currently a List of Maps. A better approach may be to return the result in a list of list of strings that places the values in 
the order they appear in the schema of the table).

The code for our TT2BDD transformation is in 
mde/tt2bdd/ModelToModelTransformer/src/TT2BDD_ContextGenerator.java

I'm a little interested to know what you think about an approach like this to performing the model to model transformations. 
Ideally the api would also allow for a way to actually run the Prolog constraints on the resulting model before passing it to 
vm2t. We're also interested in finishing the Prolog M2M transformation for this example. The Java code is structured very 
similarly to how we were trying to solve the Prolog version. Doing it this way allowed for us to see the flaws in our algorithm
and have fine control over the output in the tables to give to vm2t.
