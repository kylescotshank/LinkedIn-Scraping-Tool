package getLinkedInURLs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class getURLs {

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		//checkUrls("Spotifytest.csv", "Spotify");
		getInfo("Facebook");

	}
	public static void checkUrls(String filename, String company) {
		Scanner urls = null;
		try {
			urls = new Scanner(new File(filename));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			FileWriter clean = null;
			while (urls.hasNext()) {
				Document urlDoc = null;

				String url = urls.next();

				urlDoc = Jsoup.connect("http://" + url).ignoreHttpErrors(true).get();
				Elements content = urlDoc.select("dl#overview");
				String current = null;
				if (content.text().indexOf("Past") > 0) {
					current = content.text().substring(0, content.text().indexOf("Past"));

					//System.out.println(current);
					clean = new FileWriter(company + "Cleaned.csv");

					if (current.contains("at " + company)) {
						clean.append(url);
						clean.append(',');
						clean.append('\n');
						clean.flush();
					}
				}
			}
			clean.close();
			System.out.println("Done!");
		}

		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed");
		}





	}
	public static String toSentenceCase(String a) {
		String b = a;
		a = a.toLowerCase(); //send whole string to lower case
		int space = a.indexOf(' '); //get space between first and last
		if (space > 0 && space < a.length() - 1) {
			String firstNameInitial = a.charAt(0) + "";
			String lastNameInitial = a.charAt(space + 1) + ""; 
			firstNameInitial = firstNameInitial.toUpperCase();
			lastNameInitial = lastNameInitial.toUpperCase();
			String firstNameWithoutFirst = a.substring(1, space);
			String lastNameWithoutFirst = a.substring(space + 2);
			a = firstNameInitial + firstNameWithoutFirst + " " + lastNameInitial + lastNameWithoutFirst;
			b = a;
		}
		return b;
	}
	public static void getInfo(String abc) {

		Scanner first = null;
		String nameZ = abc;
		//Scanner last

		try {
			first = new Scanner(new File("census-derived-all-first.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String[] names = new String[5163];
		for (int i = 0; i < names.length; i++) {
			names[i] = "site:linkedin.com" + "%20" + first.next().toLowerCase() + "%20" + '"' + "at+ " + nameZ + '"';
			//System.out.println(names[i]);
		}
		//Document doc = null;
		try {

			//for each employee, search google, gather links, title, body of search results
			FileWriter writer = new FileWriter(nameZ + "Random.csv");

			Document googleDoc = null;
			for (int t = 0; t < names.length; t++) {

				String url = "https://www.bing.com/search?q=" + names[t];		
				googleDoc = Jsoup.connect(url).userAgent("Chrome").timeout(0).maxBodySize(0).get();
				//System.out.println(googleDoc.text());
				Elements links = googleDoc.select("[class=b_algo]");
				for (Element a : links) {
					Elements titles = a.select("h2");
					String name = titles.text();
					name = name.replace(" | LinkedIn", "");

					//System.out.println("Name:" + name);

					Elements bodies = a.select("div.b_caption");
					String body = bodies.text();
					//System.out.println("Body:" + body);

					Elements prof = a.select("div.b_attribution");
					String link = prof.text();
					
					//System.out.println("Link:" + link);

					if ((link.contains("/in/") || link.contains("/pub/")) && !link.contains("/dir/") && !link.contains("/company/")) {
						if (!name.contains("profiles") && !name.contains("Profiles")) {
							if (body.contains("at " + nameZ)) {
								boolean flip;
								try {
									flip = true;								
									if (name.contains(",")) {
										name = name.substring(0, name.indexOf(','));
									}
									else if (name.contains("-")) {
										flip = false;
									}
									
								}
								catch (NullPointerException e ){
									flip = false;
								}
								if (flip) {		

									System.out.println(toSentenceCase(name) + ", " + link);
									writer.append(toSentenceCase(name));
									writer.append(',');
									writer.append(link);
									writer.append('\n');
									
								}
							}
						}
					}
				}
			}
			writer.flush();
			writer.close();
			System.out.println("DONE");
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed");
		}
	}
}
