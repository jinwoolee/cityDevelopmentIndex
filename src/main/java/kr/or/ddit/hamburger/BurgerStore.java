package kr.or.ddit.hamburger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.or.ddit.hamburger.dao.BurgerDao;
import kr.or.ddit.hamburger.dao.BurgerDaoImpl;
import kr.or.ddit.hamburger.model.BurgerStoreVo;



public class BurgerStore {
	private static final String BURGER_KING = "BURGER KING";	
	private static final String LOTTERIA = "LOTTERIA";
	private static final String MACDONALD = "MACDONALD";
	private static final String KFC = "KFC";
	
	static Logger logger = LoggerFactory.getLogger(BurgerStore.class);
	static final String KAKAO_REST_KEY = "608acba5cf1f813c93be3eba120d9124"; 
	
	
	private BurgerDao burgerDao = new BurgerDaoImpl(); 
	
	
	public static void main(String[] args) throws IOException {
		BurgerStore burgerStore = new BurgerStore();
//		burgerStore.getBurgerKing();			//버거킹
//		burgerStore.getMacdolands();			//맥도날드
//		burgerStore.getKfc();					//kfc
		
//		burgerStore.getLotteria_byStoreNm();	//롯데리아
		
		burgerStore.getLotteria();	//롯데리아
	
		//burgerStore.getAddrInfo("부산광역시 기장군 기장읍 280-1",  "test");
	}
	
	//롯데리아 단체주문서비스 메뉴 제공 매장 주소 : 주소 뒷부분이 반복적으로 나오는 에러가 있다 ㅠ_ㅠ
	private void getLotteria() throws IOException{
		
		int lastPage = 150;
		
		for(int page = 1; page <= lastPage; page++) {
			Document doc = Jsoup.connect("http://www.lotteriamall.com/party/group_party.asp")
							.data("page", Integer.toString(page))
							.parser(Parser.htmlParser())
							.get();
			
			Elements trs = doc.select("tbody tr");
			for(Element element : trs) {
				
				String storeNm = element.select("th").text();
				
				logger.debug("storeNm : {}", storeNm);
				
				getLotteriaAddr(storeNm);
			}
		}
	}
	
	private void getAddrInfo(String storeCategory, String storeNm) {
		try {
			String jsonStr = Jsoup.connect("https://dapi.kakao.com/v2/local/search/keyword.json")
									.header("Authorization", "KakaoAK " + KAKAO_REST_KEY)
									.data("query", storeNm)
									.ignoreContentType(true)
									.execute().body();
			
			
			JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
			
			if(jsonObject.getAsJsonArray("documents").size() > 0) {
				
				JsonObject addrJson = jsonObject.getAsJsonArray("documents").get(0).getAsJsonObject();
			
				String road_address = addrJson.get("road_address_name").getAsString();
				
				getAddrInfo(road_address, storeCategory, storeNm);
				
			}
			else {
				logger.debug("{} size : {}", storeNm, jsonObject.getAsJsonArray("documents").size());
			}
				
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getAddrInfo(String addr, String storeCategory, String storeNm) {
		if(addr == null || addr.equals(""))
			return;
		
		try {
			String jsonStr = Jsoup.connect("https://dapi.kakao.com/v2/local/search/address.json")
					.header("Authorization", "KakaoAK " + KAKAO_REST_KEY)
					.data("query", addr)
					.ignoreContentType(true)
					.execute().body();
			
			
			JsonObject jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
			
			if(jsonObject.getAsJsonArray("documents").size() > 0) {
				
				JsonObject addrJson = jsonObject.getAsJsonArray("documents").get(0).getAsJsonObject();
			
				try {
					addrJson = addrJson.getAsJsonObject("road_address");
				}catch(Exception e) {
					//도로주소가 null일 경우 지번주소
					addrJson = addrJson.getAsJsonObject("address");
				}
				
				BurgerStoreVo burgerStoreVo = new BurgerStoreVo(addrJson.get("region_1depth_name").toString().replaceAll("\"", ""),
															    addrJson.get("region_2depth_name").toString().replaceAll("\"", ""),
															    storeCategory,
															    storeNm,
															    Double.parseDouble(addrJson.get("x").toString().replaceAll("\"", "")),
															    Double.parseDouble(addrJson.get("y").toString().replaceAll("\"", "")));
				
				logger.debug("{}", burgerStoreVo);
				burgerDao.insertBurgerStore(burgerStoreVo);
			}
			else {
				logger.debug("{} : {} - {} ", "no address info", storeNm, addr);
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
		
		//매장 리스트(주소X, 약 700여개)
		//http://www.lotteria.com/Shop/Shop_Ajax.asp?PageSize=3000#
		
		//매장 검색(주소O)
		//https://mobilehome.lotteria.com/store/search
		
		//단체 주문(약 970여개)
		//http://www.lotteriamall.com/party/group_party.asp
		
		//매장명 불러오기
		Response response = Jsoup.connect("http://www.lotteria.com/Shop/Shop_Ajax.asp")
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
								.execute();
		
		Elements elements = Jsoup.parse(response.body()).select(".searchset_btn");
		
		logger.debug("elements size : {}", elements.size());
		
		for(Element element : elements) {
			
			String href = element.attr("href");
			String storeNm = href.substring(href.indexOf("=")+1, href.indexOf("&"));
			storeNm = URLDecoder.decode(storeNm, "UTF-8");

			getAddrInfo(LOTTERIA, storeNm);
			
		}		
	}
	
	private void getLotteriaAddr(String storeNm) throws IOException{
		
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
		
		//점포검색시 비슷한 다른 점포가 검색될 가능성이 존재
		if(jsonArray.size() >= 1) {
			
			JsonElement jsonElement = jsonArray.get(0);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String addr = jsonObject.get("si").toString().replace("\"", "") + " " + jsonObject.get("gu").toString().replace("\"", "") + " " +
						  jsonObject.get("dong").toString().replace("\"", "") + " " + jsonObject.get("bunji").toString().replace("\"", "");
			
			getAddrInfo(addr, LOTTERIA, storeNm);
		}	
		else {
			logger.debug("{} : ", storeNm, jsonArray.toString());
		}
		
	}

	
	
	private void getKfc() throws IOException {
		
		//https://www.kfckorea.com/kfc/interface/selectStoreList POST
		//sido_search = A0181~A01817
		//gugun_search = sido_search + 세자리 인덱스
		//device=WEB&store_search=&sido_search=A0181&gugun_search=A018001&show_search=Y&store_show_type=&sales_code_search=&initYn=N&lat_search=&lng_search=&rows=20
			
		
		for(int i = 1; i <= 17; i++) {
			for(int j = 0; j <= 50; j++) {
				
				String sido = "A018" + i;
				
				
				String gugun = "A018" + i + String.format("%03d", j);
				
				//강남역코드가 잘못되어있다
				if(gugun.equals("A0181001"))
					gugun = "A018001";
				
				Document doc = Jsoup.connect("https://www.kfckorea.com/kfc/interface/selectStoreList")
						.data("device", "WEB")
						.data("store_search", "")
						.data("sido_search", sido)
						.data("gugun_search", gugun)
						
						
						
						.data("show_search", "Y")
						.data("store_show_type", "")
						.data("sales_code_search", "")
						.data("initYn", "N")
						.data("lat_search", "")
						.data("lng_search", "")
						.data("rows", "50")
						.ignoreContentType(true)
						.post();
				
				
				JsonArray jsonArray = JsonParser.parseString(doc.body().text()).getAsJsonObject().get("rows").getAsJsonArray();
				Iterator<JsonElement> iterator = jsonArray.iterator();
				
				while(iterator.hasNext()) {
					JsonElement jsonElement = iterator.next();
					JsonObject store = jsonElement.getAsJsonObject();
					/*logger.debug("jsonElement : {}, {}, {}, {}, {}", store.get("totpage").toString().replace("\"", ""), store.get("store_name").toString().replace("\"", ""), 
														store.get("addr_road").toString().replace("\"", ""), store.get("addr_si").toString().replace("\"", ""), store.get("addr_gu").toString().replace("\"", ""));*/
					
					BurgerStoreVo burgerStoreVo = new BurgerStoreVo(store.get("store_address_sido").toString().replace("\"", "").trim(),
															   store.get("store_address_gugun").toString().replace("\"", ""),
															   KFC,
															   store.get("store_name").toString().replace("\"", ""),
															   Double.parseDouble(store.get("store_latitude").toString().replace("\"", "")),
															   Double.parseDouble(store.get("store_longitude").toString().replace("\"", "")));
					
					burgerDao.insertBurgerStore(burgerStoreVo);					
				}
			}
		}
		
	}

	private void getMacdolands() throws IOException {
		
		for(int i = 1; i <= 85; i++) {
			Document doc = Jsoup.connect("https://www.mcdonalds.co.kr/kor/store/list.do")
								.data("page", Integer.toString(i))
								.data("lat", "NO")
								.data("lng", "NO")
								.data("searchWord", "")
								.data("search_options", "")
								.parser(Parser.htmlParser())
								.post();
			
			Elements storeList = doc.select(".tdName");
			
			logger.debug("{}", i);
			
			for(Element store : storeList) {
				getAddrInfo(store.select(".road").text(), MACDONALD, store.select(".tit a").text());
			}
		}
	}
	
	private void getBurgerKing() throws UnsupportedEncodingException, IOException {
		String param = "{\"header\":{\"error_code\":\"\",\"error_text\":\"\",\"info_text\":\"\",\"login_session_id\":\"\",\"message_version\":\"\",\"result\":true,\"trcode\":\"BKR0001\",\"ip_address\":\"\",\"platform\":\"02\",\"id_member\":\"\",\"auth_token\":\"\"},\"body\":{\"addrSi\":\"\",\"addrGu\":\"\",\"dirveTh\":\"\",\"dlvyn\":\"\",\"kmonYn\":\"\",\"kordYn\":\"\",\"oper24Yn\":\"\",\"parkingYn\":\"\",\"distance\":\"\",\"sortType\":\"\",\"storCoordX\":\"\",\"storCoordY\":\"\",\"storNm\":\"점\"}}";
		
		Document doc = Jsoup.connect("https://www.burgerking.co.kr/BKR0001.json?message="+URLEncoder.encode(param, "UTF-8"))
							.ignoreContentType(true)
							.get();
			
		JsonObject jsonObject = JsonParser.parseString(doc.body().text()).getAsJsonObject();
		
		JsonArray storeList = jsonObject.get("body").getAsJsonObject().get("storeList").getAsJsonArray();

		Iterator<JsonElement> iterator = storeList.iterator();
		
		while(iterator.hasNext()) {
			JsonElement storeElement = iterator.next();
			JsonObject store = storeElement.getAsJsonObject();
			
			getAddrInfo(store.get("ADDR_1").toString().replaceAll("\"", ""), BURGER_KING, store.get("STOR_NM").toString().replaceAll("\"", ""));			
		}
	}
}
