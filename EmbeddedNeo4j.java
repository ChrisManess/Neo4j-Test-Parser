import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.UniqueFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.util.FileUtils;

public class EmbeddedNeo4j {
	private static final String DB_PATH = "/Users/chrismaness/Documents/workspace";
	String greeting;
	// START SNIPPET: vars
	GraphDatabaseService graphDb;
	Node tweet;
	Node user;
	Relationship relationship;
	ArrayList<Tweet> tweets = new ArrayList<Tweet>();

	// END SNIPPET: vars

	// START SNIPPET: createReltype
	private static enum RelTypes implements RelationshipType {
		TWEETED,RETWEETED,MENTIONED
	}

	// END SNIPPET: createReltype

	public static void main(final String[] args) {
		EmbeddedNeo4j hello = new EmbeddedNeo4j();
		hello.prepareArrayList();
		hello.createDb();
		//hello.removeData();
		hello.shutDown();
		hello.clearDb();
	}

	private void prepareArrayList() {
		// TODO Auto-generated method stub
		for(int i=0; i<10; i++){
			tweets.add(new Tweet());
		}
	}

	void createDb() {
		clearDb();
		// START SNIPPET: startDb
		graphDb = new EmbeddedGraphDatabase(DB_PATH);
		registerShutdownHook(graphDb);
		// END SNIPPET: startDb

		// START SNIPPET: transaction
		Transaction tx = graphDb.beginTx();
		try {
			
			/*
			// Mutating operations go here
			// END SNIPPET: transaction
			// START SNIPPET: addData
			firstNode = graphDb.createNode();
			firstNode.setProperty("message", "Hello, ");
			secondNode = graphDb.createNode();
			secondNode.setProperty("message", "World!");

			relationship = firstNode.createRelationshipTo(secondNode,
					RelTypes.TWEETED);
			relationship.setProperty("message", "brave Neo4j ");
			// END SNIPPET: addData

			// START SNIPPET: readData
			System.out.print(firstNode.getProperty("message"));
			System.out.print(relationship.getProperty("message"));
			System.out.print(secondNode.getProperty("message"));
			// END SNIPPET: readData

			greeting = ((String) firstNode.getProperty("message"))
					+ ((String) relationship.getProperty("message"))
					+ ((String) secondNode.getProperty("message"));

			// START SNIPPET: transaction*/
			
			for(Tweet t : tweets){
				
				tweet = null;
				user = null;
				
				Map<String,String> hm = t.getHm();
				
				//create tweet node
				//get each property and add it to the node as a property
				tweet = this.createTweetnode(graphDb);
				
				//if user exists
				//get user 
				user = this.getOrCreateUserWithUniqueFactory(hm.get("User Id"), graphDb);
				
				relationship = user.createRelationshipTo(tweet, RelTypes.TWEETED);
				
			}
			
			//print out all user nodes
			
			
			tx.success();
		} finally {
			tx.finish();
		}
		// END SNIPPET: transaction
	}
	
	private Node createTweetnode(GraphDatabaseService graphDb) {
		// TODO Auto-generated method stub
		
		Node n;
		
		n = graphDb.createNode();
		n.setProperty("type", "tweet");
		n.setProperty("tweetId", "123456");
		n.setProperty("tweet", "This is a tweet dang it");
		
		return n;
	}

	public Node getOrCreateUserWithUniqueFactory( String userId, GraphDatabaseService graphDb )
	{
	    UniqueFactory<Node> factory = new UniqueFactory.UniqueNodeFactory( graphDb, "users" )
	    {
	        @Override
	        protected void initialize( Node created, Map<String, Object> properties )
	        {
	            created.setProperty( "userId", properties.get( "userId" ) );
	        }
	    };
	 
	    return factory.getOrCreate( "userId", userId );
	}

	private void clearDb() {
		try {
			FileUtils.deleteRecursively(new File(DB_PATH));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void removeData() {
		Transaction tx = graphDb.beginTx();
		try {
			// START SNIPPET: removingData
			// let's remove the data
			tweet.getSingleRelationship(RelTypes.TWEETED, Direction.OUTGOING)
					.delete();
			tweet.delete();
			user.delete();
			// END SNIPPET: removingData

			tx.success();
		} finally {
			tx.finish();
		}
	}

	void shutDown() {
		System.out.println();
		System.out.println("Shutting down database ...");
		// START SNIPPET: shutdownServer
		graphDb.shutdown();
		// END SNIPPET: shutdownServer
	}

	// START SNIPPET: shutdownHook
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	// END SNIPPET: shutdownHook

}