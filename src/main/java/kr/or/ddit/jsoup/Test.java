package kr.or.ddit.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class Test {
	public static void main(String[] args) throws IOException {
		 Document doc = Jsoup.connect("http://www.hrd.go.kr/jsp/HRDP/HRDPO00/HRDPOA60/HRDPOA60_1.jsp")
				 .data("authKey", "OlkDJhEFgFYsNqG3aY2BimgnjFbBvZDF")
				 .data("returnType", "XML")
				 .data("outType", "1")
				 .data("pageNum", "1")
				 .data("pageSize", "10")
				 .data("srchTraStDt", "20200208")
				 .data("srchTraEndDt", "20200508")
				 .data("sort", "ASC")
				 .data("sortCol", "TR_STT_DT")
				 .data("srchTraArea1", "30")
				 .data("srchTraArea2", "30170")
				 .parser(Parser.xmlParser()).get();
		 
		 Elements elements = doc.select("HRDNet");
		 for(Element element : elements)
			 System.out.println(element);
		 
	}
}
