#This comes from a Java program I wrote over the summer for my job at HiQ Labs.
#It scrapes Google's search results using Nokogiri to grab both the names 
#and public LinkedIn urls of a specific company's employees.


require 'rubygems'
require 'nokogiri'
require 'open-uri'


#using Nokogiri, grab the urls and names of the employees
def grabHTML(urls)
	output = File.open("companyInfo.txt", 'w')
	urls.each {|url| puts url
	links = Nokogiri::HTML(open(url))
	links.css('h3.r a').each do |link|
    	name = link.content.sub! ' | LinkedIn', '\n' 
    	link = 'https://google.com' << link['href']
    	puts name
    	puts link
    	unless name.include? "Profiles" #results sometimes inlude list of profiles--not what I want
    		output.write(name)
    		output.write('\n')
    		output.write(link)
    		output.write('\n')
    	end	
    end
}
end


#reads in all first names, generates search queries for each name and company
def gen_Queries(company_name)

	fname = 'census-derived-all-first.csv'
	text = File.open(fname).read
	text.gsub!(/\r\n?/, "\n")
	i = 0
	urls = Array.new(5162)
	text.each_line do |line|
		line = 'http://www.google.com/search?q=site:linkedin.com%20' + line + "%20" + '"' + "at+ " + company_name + '"' 
		urls[i] = line
		i += 1
	end
	grabHTML(urls)
end


gen_Queries('Facebook')




