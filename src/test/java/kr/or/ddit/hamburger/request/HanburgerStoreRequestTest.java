package kr.or.ddit.hamburger.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import kr.or.ddit.hamburger.BurgerStore;
import kr.or.ddit.hamburger.dao.BurgerDao;
import kr.or.ddit.hamburger.dao.BurgerDaoImpl;

public class HanburgerStoreRequestTest {


	
	/** 
	 * Method   : test
	 * 작성자 : jw
	 * 변경이력 :  
	 * Method 설명 : 버거킹 점포 크롤링 테스트 
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	@Test
	public void test() throws UnsupportedEncodingException, IOException {
		/***given***/
		BurgerStore burgerStore = new BurgerStore();
		BurgerDao burgerDao = new BurgerDaoImpl();
		burgerDao.deleteBurgerStore(BurgerStore.BURGER_KING);

		/***when***/
		
		burgerStore.getBurgerKing();

		/***then***/
	}

}
