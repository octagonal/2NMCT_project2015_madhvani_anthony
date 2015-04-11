require 'nokogiri'
require 'open-uri'

user_agent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"

cities = ["antwerpen","brugge","gent","brussel","hasselt","kortrijk","leuven"]
overview_prefix = "http://ikot.be/kot-"
props = ["Totale huurprijs","Oppervlakte"]

File.open("kot.csv", 'w') { 
  |file| 
  cities.each { 
    |city| 
    puts "Parsing: " + overview_prefix + city 
    doc = Nokogiri::HTML(open(overview_prefix + city, 'User-Agent' => user_agent))
    doc.css(".result-street a").each {
      |n| 
      print n.attribute("href"); print "\n";
      kot = Nokogiri::HTML(open(overview_prefix + n.attribute("href"), 'User-Agent' => user_agent))
      row = city + ";" + n.attribute("href") + ";" 
      row << kot.css(".room-section table tr").select {
        |prop|
        prop.inner_text.match(Regexp.union(props))
      }.map {
        |pr|
        pr.css("td:nth-child(2)").inner_text.gsub(/[^0-9]/,"")
      }.join(";")
      row << ";" 
      points = kot.css("#main-content > script:nth-child(1)").inner_text.split(";")[0..1]
      row << points.map{|point| point.gsub(/[^0-9|\.]/,"")}.join(";")
      puts "Row: " + row
      file.write(row + "\n") 
    }
    print "\n"
  }
}
