import java.util.HashMap;
import java.util.Map;


public class Tweet {

	Map<String,String> hm = new HashMap();

	public Tweet() {
		
		hm.put("Tweet Id", "1234");
		hm.put("Text", "this text");
		hm.put("User Id", "123");
		hm.put("User Name", "NameOfUser");
		
	}
	
	public Map<String,String> getHm(){
		return hm;
	}
	
}
