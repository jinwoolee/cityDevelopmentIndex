package kr.or.ddit.hamburger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class BurgerStore {
	static Logger logger = LoggerFactory.getLogger(BurgerStore.class);
	static final String KAKAO_REST_KEY = "608acba5cf1f813c93be3eba120d9124"; 
	
	
	public static void main(String[] args) throws IOException {
		BurgerStore burgerStore = new BurgerStore();
//		burgerStore.getBurgerKing();			//버거킹
//		burgerStore.getMacdolands();			//맥도날드
//		burgerStore.getKfc();					//kfc
		burgerStore.getLotteria_byStoreNm();	//롯데리아
		
		
		//burgerStore.getAddrInfo("대전광역시 서구 계룡로 630");
		
	}
	
	//롯데리아 단체주문서비스 메뉴 제공 매장 주소 : 주소 뒷부분이 반복적으로 나오는 에러가 있다 ㅠ_ㅠ
	/*private void getLotteria() throws IOException{
		
		int lastPage = 2;
		
		for(int page = 1; page <= lastPage; page++) {
			Document doc = Jsoup.connect("http://www.lotteriamall.com/party/group_party.asp")
							.data("page", Integer.toString(page))
							.parser(Parser.htmlParser())
							.get();
			
			Elements trs = doc.select("tbody tr");
			for(Element element : trs) {
				logger.debug("{} : {}", element.select("th").text(), element.select("tr td:nth-child(2) span").text());
			}
		}
	}*/
	
	
	
	/*
	 * {"documents":[
	{"address":
		{"address_name":"대전 서구 용문동 589-12",
		 "b_code":"3017010500",
		 "h_code":"3017055000",
		 "main_adderss_no":"",
		 "main_address_no":"589",
		 "mountain_yn":"N",
		 "region_1depth_name":"대전",
		 "region_2depth_name":"서구",
		 "region_3depth_h_name":"용문동",
		 "region_3depth_name":"용문동",
		 "sub_adderss_no":"",
		 "sub_address_no":"12",
		 "x":"127.391923837398",
		 "y":"36.3390091103489",
		 "zip_code":""},
	"address_name":"대전 서구 계룡로 630",
	"address_type":"ROAD_ADDR",
	"road_address":{
		"address_name":"대전 서구 계룡로 630",
		"building_name":"수정빌딩",
		"main_building_no":"630",
		"region_1depth_name":"대전",
		"region_2depth_name":"서구",
		"region_3depth_name":"용문동",
		"road_name":"계룡로",
		"sub_building_no":"",
		"undergroun_yn":"",
		"underground_yn":"N",
		"x":"127.391923837398",
		"y":"36.3390091103489",
		"zone_no":"35300"},
	"x":"127.391923837398",
	"y":"36.3390091103489"}],
	
	"meta":
		{"is_end":true,
		 "pageable_count":1,
		 "total_count":1}
}
	 */
	private void getAddrInfo(String addr) {
		if(addr == null || addr.equals(""))
			return;
		
		try {
			String jsonStr = Jsoup.connect("https://dapi.kakao.com/v2/local/search/address.json")
					.header("Authorization", "KakaoAK " + KAKAO_REST_KEY)
					.data("query", addr)
					.ignoreContentType(true)
					.execute().body();
			
			
			JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
			
			//jsonObject.getAsJsonArray("documents").get(0).getAsJsonObject().getAsJsonObject("road_address").getAsJsonObject("building_name").getAsString()
			
			if(jsonObject.getAsJsonArray("documents").size() > 0) {
				JsonObject addrJson = jsonObject.getAsJsonArray("documents").get(0).getAsJsonObject().getAsJsonObject("road_address");
				logger.debug("{} : {} {} / {} {}", addr, addrJson.get("region_1depth_name").toString().replaceAll("\"", ""),
											  addrJson.get("region_2depth_name").toString().replaceAll("\"", ""),
											  addrJson.get("x").toString().replaceAll("\"", ""),
											  addrJson.get("y").toString().replaceAll("\"", ""));
			}
			else {
				logger.debug("{}", "no address info");
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.debug("{} ??", addr );
		}
	}
	
	//롯데리아 홈페이지에서는 매장 주소를 제공하지 않음
	//단 단체주문 서비스, 모바일 페이지 매장 찾기에서 매장명으로 주소를 검색할 수는 있으나
	//위 두 사이트는 퀵오더 혹은 단체 주문 서비스가 가능한 매장에 대해서만 검색이 가능
	private void getLotteria_byStoreNm() throws IOException {
		
		//매장명 불러오기
		Document doc = Jsoup.connect("http://www.lotteria.com/Shop/Shop_Ajax.asp")
					.ignoreContentType(true)
					.data("Page", "1")
					.data("PageSize", "3000")
					.data("BlockSize", "10") 
					.data("SearchArea1", "") 
					.data("SearchArea2", "")
					.data("SearchType", "TEXT") 
					.data("SearchText", "")
					.data("SearchIs24H", "0") 
					.data("SearchIsWifi", "0") 
					.data("SearchIsDT", "0")
					.data("SearchIsHomeService", "0") 
					.data("SearchIsGroupOrder", "0") 
					.data("SearchIsEvent", "0")
					.data("SearchIsFreshChicken", "0") 
					.data("SearchIsRiaOrder", "0")
					.parser(Parser.htmlParser())
					.post();
		
		List<String> storeList = new ArrayList<String>();
		
		Elements elements = doc.select(".first a");
		for(Element element : elements) {
			storeList.add(element.text());
			
			logger.debug("{} : {}", element.text(), getLotteriaAddr(element.text()));
		}		
	}
	
	private String getLotteriaAddr(String storeNm) throws IOException{
		
		Document doc = Jsoup.connect("https://mobilehome.lotteria.com/store/search")
						.ignoreContentType(true)
						.data("input", storeNm)
						.data("searchSize", "")
						.data("searchBy", "nm")
						.data("xCenterPoint", "0")
						.data("yCenterPoint", "0")
						.data("delivery", "false")
						.data("pickup", "")
						.data("maxDistance", "")
						.data("orderOnly", "false")
						.data("deviceCode", "PC")
						.parser(Parser.htmlParser())
						.post();
		String body = doc.select("body").text();
		JsonArray jsonArray = JsonParser.parseString(body).getAsJsonArray();
		
		String addr = "";
		if(jsonArray.size() == 1) {
			
			JsonElement jsonElement = jsonArray.get(0);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			addr = jsonObject.get("si").toString().replace("\"", "") + " " + jsonObject.get("gu").toString().replace("\"", "") + " " +
						  jsonObject.get("dong").toString().replace("\"", "") + " " + jsonObject.get("bunji").toString().replace("\"", "");
			logger.debug("{}", addr);
			
			getAddrInfo(addr);
		}	
		return addr;
	}

	private void getKfc() throws IOException {
		
		List<Map<String, String>> storeList = getKfcPage(1);
		int totpage = 0;
		if(storeList.size() > 0 && Integer.parseInt(storeList.get(0).get("totpage")) > 0) {
			totpage = Integer.parseInt(storeList.get(0).get("totpage"));
		}
		
		for(int i = 0; i <= totpage; i++) {
			getKfcPage(i);
		}
	}
	
	private List<Map<String, String>> getKfcPage(int page) throws IOException {
		
		List<Map<String, String>> storeList = new ArrayList<Map<String, String>>();
		
		Document doc = Jsoup.connect("http://delivery.kfckorea.com/get.do")
				.data("id", "Store")
				.data("ac", "select")
				.data("page", Integer.toString(page))
				.ignoreContentType(true)
				.get();
		
		JsonArray jsonArray = JsonParser.parseString(doc.body().text()).getAsJsonArray();
		Iterator<JsonElement> iterator = jsonArray.iterator();
		
		while(iterator.hasNext()) {
			JsonElement jsonElement = iterator.next();
			JsonObject store = jsonElement.getAsJsonObject();
			logger.debug("jsonElement : {}, {}, {}, {}, {}", store.get("totpage").toString().replace("\"", ""), store.get("store_name").toString().replace("\"", ""), 
												store.get("addr_road").toString().replace("\"", ""), store.get("addr_si").toString().replace("\"", ""), store.get("addr_gu").toString().replace("\"", ""));
			
			Map<String, String> storeMap = new HashMap<String, String>();
			
			storeMap.put("totpage", store.get("totpage").toString().replace("\"", ""));
			storeMap.put("store_name", store.get("store_name").toString().replace("\"", ""));
			storeMap.put("addr_road", store.get("addr_road").toString().replace("\"", ""));
			storeMap.put("addr_si", store.get("addr_si").toString().replace("\"", ""));
			storeMap.put("addr_gu", store.get("addr_gu").toString().replace("\"", ""));
			
			if(! store.get("addr_road").toString().replace("\"", "").equals(""))
				logger.debug("addr_road : {}", store.get("addr_road").toString().replace("\"", ""));
				getAddrInfo(store.get("addr_road").toString());
			
			storeList.add(storeMap);
		}
		
		
		return storeList;
	}

	private void getMacdolands() throws IOException {
		
		for(int i = 1; i <= 30; i++) {
			Document doc = Jsoup.connect("https://www.mcdonalds.co.kr/kor/store/list.do")
								.data("page", Integer.toString(i))
								.data("lat", "NO")
								.data("lng", "NO")
								.data("searchWord", "점")
								.data("search_options", "")
								.parser(Parser.htmlParser())
								.post();
			
			//$(\".tdName .tit a\").text() + $(\".tdName .road\").text()
			
			Elements storeList = doc.select(".tdName");
//			Elements storeNames = doc.select(".tdName .tit");
//			Elements storeRoadAddr = doc.select(".tdName .road");
			
			for(Element store : storeList) {
				logger.debug("store : {}, roadAddr : {}", store.select(".tit").text(), store.select(".road").text());
				getAddrInfo(store.select(".road").text());
			}
		}
			
			
	}
	
	private void getBurgerKing() throws UnsupportedEncodingException, IOException {
		String param = "{\"header\":{\"error_code\":\"\",\"error_text\":\"\",\"info_text\":\"\",\"login_session_id\":\"\",\"message_version\":\"\",\"result\":true,\"trcode\":\"BKR0001\",\"ip_address\":\"\",\"platform\":\"02\",\"id_member\":\"\",\"auth_token\":\"\"},\"body\":{\"addrSi\":\"\",\"addrGu\":\"\",\"dirveTh\":\"\",\"dlvyn\":\"\",\"kmonYn\":\"\",\"kordYn\":\"\",\"oper24Yn\":\"\",\"parkingYn\":\"\",\"distance\":\"\",\"sortType\":\"\",\"storCoordX\":\"\",\"storCoordY\":\"\",\"storNm\":\"점\"}}";
		
		Document doc = Jsoup.connect("https://www.burgerking.co.kr/BKR0001.json?message="+URLEncoder.encode(param, "UTF-8"))
							.ignoreContentType(true)
							.get();
		
		
		Gson gson = new GsonBuilder().create();
		
		JsonObject jsonObject = JsonParser.parseString(doc.body().text()).getAsJsonObject();
//		logger.debug("{}", jsonObject);
//		logger.debug("{}", jsonObject.get("body"));
//		logger.debug("{}", jsonObject.get("body").getAsJsonObject().get("storeList"));
//		logger.debug("{}", jsonObject.get("body").getAsJsonObject().get("storeList").getAsJsonArray());
		
		JsonArray storeList = jsonObject.get("body").getAsJsonObject().get("storeList").getAsJsonArray();

		Iterator<JsonElement> iterator = storeList.iterator();
		int index = 1;
		while(iterator.hasNext()) {
			JsonElement storeElement = iterator.next();
			JsonObject store = storeElement.getAsJsonObject();
			
//			logger.debug("{} : {} {} / {} {}", index++, 
//												store.get("STOR_NM"),
//												store.get("ADDR_1"),
//												store.get("STOR_COORD_X"),
//												store.get("STOR_COORD_Y"));
			
			getAddrInfo(store.get("ADDR_1").toString());
		}
	}
}
