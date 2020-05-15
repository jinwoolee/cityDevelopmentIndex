package kr.or.ddit.hamburger.dao;

import kr.or.ddit.hamburger.model.BurgerStoreVo;

public interface BurgerDao {

	/** 
	 * Method   : insertBurgerStore
	 * 작성자 : jw
	 * 변경이력 : 
	 * @param burgerStoreVo
	 * @return 
	 * Method 설명 : 점포 입력 
	 */
	int insertBurgerStore(BurgerStoreVo burgerStoreVo);

	/** 
	 * Method   : deleteBurgerKing
	 * 작성자 : jw
	 * 변경이력 :  
	 * Method 설명 : 점포 삭제 
	 * @param burgerKing 
	 */
	int deleteBurgerStore(String burgerKing);
}
