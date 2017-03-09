import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String name="pulin12535salinha";
		Pattern p = Pattern.compile("[^0-9]*");
		Matcher m = p.matcher(name);

		if(m.find())
		    System.out.println(m.group()); 
	}

}
