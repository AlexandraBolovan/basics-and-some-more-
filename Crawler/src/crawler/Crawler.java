/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;
// the exercise : Print the URLS of the pages that contain the word " Erasmus" from the website "ing.pub.ro"
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jsoup.Jsoup;     // download Jsoup - java library for parsing HTML pages !!!!
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

    public static Database db = new Database();   // creating a database for the urls we parse in the root
    
    public static void main(String[] args) throws SQLException, IOException {
        db.runSql2("TRUNCATE Record;");   // preserve the structure of the table for future use.ideal for cleaning out data from a temporary table
        processPage("http://www.ing.pub.ro/en/"); // the "root" URL (in english)
    }
    public static void processPage(String URL) throws SQLException, IOException{
		//check if the given URL is already in database
		String sql = "select * from Record where URL = '"+URL+"'";
		ResultSet rs = db.runSql(sql);
		if(rs.next()){
 
		}else{
			//store the URL to database to avoid parsing again
			sql = "INSERT INTO  `Crawler`.`Record` " + "(`URL`) VALUES " + "(?);"; //"Crawler" = name of db , "Record" = name of table
			PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, URL);
			stmt.execute();
 
			//get useful information
			Document doc = Jsoup.connect("http://www.ing.pub.ro/en/").get();
 
			if(doc.text().contains("Erasmus")){ // if the page has the word " Erasmus" , it's printed
				System.out.println(URL);
			}
 
			//get all links and recursively call the processPage method
			Elements questions = doc.select("a[href]");
			for(Element link: questions){
				if(link.attr("href").contains("ing.pub.ro/en/"))
					processPage(link.attr("abs:href"));
			}
		}
	}
}


